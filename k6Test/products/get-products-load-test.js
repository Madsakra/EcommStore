import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 500,
  duration: '30s',
};

export default function () {
  // 1. LOGIN
  const loginPayload = JSON.stringify({
    identifier: 'user3',
    password: 'W@rrio8790',
  });

  const loginRes = http.post('http://localhost:8080/auth/login', loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'token received': (r) => r.json('token') !== undefined,
  });

  const token = loginRes.json('token');

  // 2. USE JWT TO QUERY /products
  const queryParams = '?minPrice=10&maxPrice=100&page=0&size=10'; // optional filters
  const productsRes = http.get(`http://localhost:8080/products${queryParams}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  check(productsRes, {
    'products status is 200': (r) => r.status === 200,
    'products returned': (r) => r.json('content') !== undefined,
  });

  sleep(1);
}