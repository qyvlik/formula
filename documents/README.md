# formula

## benchmark with wrk

### eval

```bash
wrk -t16 -c256 -d30s --latency "http://127.0.0.1:8120/api/v1/formula/debug?formula=binance_btc_usdt-upbit_btc_usdt"
```

#### variable service backend is redis

```text
Running 30s test @ http://127.0.0.1:8120/api/v1/formula/debug?formula=binance_btc_usdt-upbit_btc_usdt
  16 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    76.57ms   51.45ms   1.03s    97.17%
    Req/Sec   203.38     54.74   470.00     70.48%
  Latency Distribution
     50%   70.08ms
     75%   77.31ms
     90%   87.11ms
     99%  309.12ms
  96083 requests in 30.10s, 43.27MB read
  Socket errors: connect 19, read 231, write 0, timeout 0
Requests/sec:   3192.19
Transfer/sec:      1.44MB
```

#### variable service backend is memory

```text
Running 30s test @ http://127.0.0.1:8120/api/v1/formula/debug?formula=binance_btc_usdt-upbit_btc_usdt
  16 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    54.10ms   92.87ms   1.90s    98.14%
    Req/Sec   333.06     94.04     0.99k    84.56%
  Latency Distribution
     50%   42.55ms
     75%   45.88ms
     90%   51.24ms
     99%  474.39ms
  158831 requests in 30.08s, 71.37MB read
  Socket errors: connect 19, read 165, write 0, timeout 0
Requests/sec:   5280.47
Transfer/sec:      2.37MB
```

### convert

```bash
wrk -t16 -c256 -d30s --latency "http://127.0.0.1:8120/api/v1/formula/convert?from=HT&to=OKB&value=1"
```

#### variable service backend is redis

```text
Running 30s test @ http://127.0.0.1:8120/api/v1/formula/convert?from=HT&to=OKB&value=1
  16 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   437.14ms  105.72ms 875.18ms   75.75%
    Req/Sec    36.88     30.42   148.00     75.07%
  Latency Distribution
     50%  406.15ms
     75%  481.66ms
     90%  601.40ms
     99%  741.44ms
  15804 requests in 30.10s, 7.11MB read
  Socket errors: connect 19, read 232, write 0, timeout 0
Requests/sec:    525.00
Transfer/sec:    241.99KB
```

#### variable service backend is memory

```text
Running 30s test @ http://127.0.0.1:8120/api/v1/formula/convert?from=HT&to=OKB&value=1
  16 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   124.58ms  128.34ms   1.24s    87.70%
    Req/Sec   150.60     46.49   380.00     67.38%
  Latency Distribution
     50%   84.92ms
     75%  128.57ms
     90%  289.22ms
     99%  630.02ms
  72117 requests in 30.08s, 32.35MB read
  Socket errors: connect 19, read 134, write 0, timeout 0
Requests/sec:   2397.31
Transfer/sec:      1.08MB
```

---

[wg/wrk](https://github.com/wg/wrk)