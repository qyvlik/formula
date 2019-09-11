# formula

## benchmark with wrk

```bash
wrk -t16 -c256 -d30s --latency "http://127.0.0.1:8120/api/v1/formula/eval?formula=upbit_eos_krw/usd_in_krw-okex_eos_usdt"
```

```text
Running 30s test @ http://127.0.0.1:8120/api/v1/formula/eval?formula=upbit_eos_krw/usd_in_krw-okex_eos_usdt
  16 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    34.71ms   10.91ms 173.40ms   79.74%
    Req/Sec   428.94    119.85   831.00     70.28%
  Latency Distribution
     50%   33.76ms
     75%   38.68ms
     90%   45.03ms
     99%   78.36ms
  167130 requests in 30.06s, 32.70MB read
  Socket errors: connect 19, read 215, write 0, timeout 237
Requests/sec:   5559.47
Transfer/sec:      1.09MB
```

---

[wg/wrk](https://github.com/wg/wrk)