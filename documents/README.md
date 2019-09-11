# formula

## eval

```bash
ab -n 4 -c 1 "http://localhost:8120/api/v1/formula/debug?formula=upbit_eos_krw/usd_in_krw-okex_eos_usdt"
```

```bash
ab -n 1024 -c 128 "http://localhost:8120/api/v1/formula/debug?formula=upbit_eos_krw/usd_in_krw-okex_eos_usdt"
```

```bash
ab -n 1024 -c 128 "http://localhost:8120/api/v1/formula/eval?formula=upbit_eos_krw/usd_in_krw-okex_eos_usdt"
```