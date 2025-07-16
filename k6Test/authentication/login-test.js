import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 500,          // number of virtual users
  duration: '30s',    // test duration
};

export default function () {
  const url = 'http://localhost:8080/auth/login';
  const payload = JSON.stringify({
    identifier: 'user3',
    password: 'W@rrio8790',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  let res = http.post(url, payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'token received': (r) => r.json('token') !== undefined,
  });

  sleep(1); // simulate user think time
}
