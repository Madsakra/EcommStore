import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100, // adjust based on desired load
  duration: '30s',
};

export default function () {
  // Step 1: Login
  const loginPayload = JSON.stringify({
    identifier: 'user3',
    password: 'W@rrio8790',
  });

  const loginHeaders = {
    headers: { 'Content-Type': 'application/json' },
  };

  const loginRes = http.post('http://localhost:8080/auth/login', loginPayload, loginHeaders);

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'token received': (r) => r.json('token') !== undefined,
  });

  const token = loginRes.json('token');

  // Step 2: Prepare order payload
  const orderPayload = JSON.stringify([
    { "id": "815bffba-1b83-48d5-b1c1-ab6b6f0070b2", "quantity": 2 },
    { "id": "d9b856d3-b0d8-4455-a957-e34000478e2b", "quantity": 2 },
    { "id": "0c216fa6-73b6-4e13-b769-b26bc6d6166b", "quantity": 2 },
    { "id": "2a0584ae-4f31-4f24-815e-b0d7b6e6aaab", "quantity": 2 },
    { "id": "82b7fed8-1e9a-452c-ad5f-f8276253f466", "quantity": 2 },
    { "id": "6907d443-c096-4eef-b12e-1c0ffd9df87d", "quantity": 2 },
    { "id": "65d26342-0a43-4312-a213-9b159e967bb9", "quantity": 2 }
  ]);

  const orderHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const orderRes = http.post('http://localhost:8080/user/orders', orderPayload, orderHeaders);

    // order status supposed to be 201 not 200
  check(orderRes, {
    'order status is 201': (r) => r.status === 201,
    'order response has body': (r) => r.body && r.body.length > 0,
  });

  sleep(1); // simulate think time
}
