package com.example.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.product_store.authentication.repositories.AccountRepository;
import com.example.product_store.authentication.AccountValidator;
import com.example.product_store.authentication.errors.AccountAlreadyExistsException;
import com.example.product_store.authentication.errors.AccountNotValidException;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.model.Role;
import com.example.product_store.authentication.service.CreateAccountService;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class CreateAccountServiceTest {

  @Mock private AccountRepository accountRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AccountValidator accountValidator;
  @InjectMocks private CreateAccountService createAccountService;

  // TEST FOR SUCCESSFUL
  @Test
  void testAccountCreation_successful() {
    // ARRANGE
    Account input = new Account();
    Role roleTest = new Role("1", "USER");
    Set<Role> roleList = new HashSet<>();

    input.setUserName("janeDoe");
    input.setEmail("jane@gmail.com");
    input.setPassword("MyPassword123!");
    roleList.add(roleTest);
    input.setRoles(roleList);

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());

    when(passwordEncoder.encode("MyPassword123!")).thenReturn("encodedPassword");

    // No exception thrown by validator
    doNothing().when(accountValidator).execute(any());

    String result = createAccountService.execute(input);

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(accountCaptor.capture());

    Account saved = accountCaptor.getValue();

    // ASSERT LOGIN CREDENTIALS CREATED
    assertEquals("janeDoe", saved.getUserName());
    assertEquals("jane@gmail.com", saved.getEmail());
    assertEquals("encodedPassword", saved.getPassword());

    // ROLES CHECK
    Set<Role> roles = saved.getRoles();
    assertNotNull(roles);
    assertNotNull(roles);
    assertEquals(1, roles.size());
    Role savedRole = roles.iterator().next();
    assertEquals("1", savedRole.getRoleId());
    assertEquals("USER", savedRole.getRoleName());

    // ASSERT RETURNED MESSAGE
    assertEquals("User Created Successfully", result);
  }

  // TEST ACCOUNT CREATION FAIL - ACCOUNT ALREADY EXIST
  @Test
  void testAccountCreation_accountAlreadyExist() {
    // ARRANGE
    Account existing = new Account();
    existing.setUserName("johnDoe");

    when(accountRepository.findByUserName("johnDoe")).thenReturn(Optional.of(existing));

    Account input = new Account();
    input.setUserName("johnDoe");

    // ASSERT AND ACT
    assertThrows(
        AccountAlreadyExistsException.class,
        () -> {
          createAccountService.execute(input);
        });
    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - EMPTY USERNAME
  @Test
  void testAccountCreation_emptyUsername() {
    Account input = new Account();
    input.setUserName("");
    input.setEmail("jane@gmail.com");
    input.setPassword("MyPassword123!");

    when(accountRepository.findByUserName("")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Account does not have user name"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    // ✅ Check exception message
    assertEquals("Account does not have user name", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - INVALID USERNAME
  @Test
  void testAccountCreation_invalidUsername() {
    Account input = new Account();
    input.setUserName("aa");
    input.setEmail("jane@gmail.com");
    input.setPassword("MyPassword123!");

    when(accountRepository.findByUserName("aa")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Username must be alphanumeric and 3–20 characters long"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    // ✅ Check exception message
    assertEquals("Username must be alphanumeric and 3–20 characters long", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - EMPTY PASSWORD
  @Test
  void testAccountCreation_emptyPassword() {
    Account input = new Account();
    input.setUserName("janeDoe");
    input.setEmail("jane@gmail.com");
    input.setPassword("");

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Password should not be blank!"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    // ✅ Check exception message
    assertEquals("Password should not be blank!", thrown.getMessage());

    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - INVALID PASSWORD
  @Test
  void testAccountCreation_invalidPassword() {
    Account input = new Account();
    input.setUserName("janeDoe");
    input.setEmail("jane@gmail.com");
    input.setPassword("toosimple");

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(
            new AccountNotValidException(
                "Password must be at least 8 characters and include uppercase, lowercase, number,"
                    + " and special character"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });
    // ✅ Check exception message
    assertEquals(
        "Password must be at least 8 characters and include uppercase, lowercase, number, and"
            + " special character",
        thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - EMPTY EMAIL
  @Test
  void testAccountCreation_emptyEmail() {
    Account input = new Account();
    input.setUserName("janeDoe");
    input.setEmail("");
    input.setPassword("MyPassword123!");

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Email should not be blank"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    // ✅ Check exception message
    assertEquals("Email should not be blank", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST ACCOUNT CREATION FAIL - INVALID EMAIL
  @Test
  void testAccountCreation_invalidEmail() {
    Account input = new Account();
    input.setUserName("janeDoe");
    input.setEmail("jane@");
    input.setPassword("MyPassword123!");

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());

    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Invalid email format"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    // ✅ Check exception message
    assertEquals("Invalid email format", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST FOR EMPTY ROLES
  @Test
  void testAccountCreation_emptyRoles() {
    Account input = new Account();
    input.setUserName("janeDoe");
    input.setEmail("jane@gmail.com");
    input.setPassword("MyPassword123!");
    input.setRoles(Collections.emptySet()); // 👈 Empty role set

    when(accountRepository.findByUserName("janeDoe")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    // Simulate validation failure for empty roles
    doThrow(new AccountNotValidException("At least one valid role is required"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    assertEquals("At least one valid role is required", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }

  // TEST FOR INVALID ROLES
  @Test
  void testAccountCreation_invalidRole() {
    Account input = new Account();
    Role invalidRole = new Role("999", "HACKER"); // 👈 Assuming this is not allowed
    input.setUserName("johnDoe");
    input.setEmail("john@gmail.com");
    input.setPassword("MyPassword123!");
    input.setRoles(Set.of(invalidRole));

    when(accountRepository.findByUserName("johnDoe")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    doThrow(new AccountNotValidException("Invalid role: HACKER"))
        .when(accountValidator)
        .execute(any());

    AccountNotValidException thrown =
        assertThrows(
            AccountNotValidException.class,
            () -> {
              createAccountService.execute(input);
            });

    assertEquals("Invalid role: HACKER", thrown.getMessage());
    verify(accountRepository, never()).save(any());
  }
}
