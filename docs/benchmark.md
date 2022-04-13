# benchmark with wrk

## memory backend

### calculate

```bash
wrk -t8 -c256 -d120s --latency --script=post-calculate.lua http://127.0.0.1:8120/api/v1/formula/calculate
```

```text
Running 2m test @ http://127.0.0.1:8120/api/v1/formula/calculate
  8 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    19.13ms    6.52ms 175.78ms   79.23%
    Req/Sec     1.67k   189.84     2.58k    75.26%
  Latency Distribution
     50%   18.49ms
     75%   21.77ms
     90%   25.77ms
     99%   42.77ms
  1596465 requests in 2.00m, 619.95MB read
  Socket errors: connect 0, read 193, write 0, timeout 0
Requests/sec:  13293.77
Transfer/sec:      5.16MB

```

### convert

```bash
wrk -t8 -c256 -d120s --latency --script=post-convert.lua http://127.0.0.1:8120/api/v1/formula/convert
```

```text
Running 2m test @ http://127.0.0.1:8120/api/v1/formula/convert
  8 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    19.49ms   30.70ms 942.19ms   98.74%
    Req/Sec     1.81k   266.16     2.59k    73.00%
  Latency Distribution
     50%   16.78ms
     75%   20.18ms
     90%   24.57ms
     99%   54.15ms
  1722231 requests in 2.00m, 721.34MB read
  Socket errors: connect 0, read 215, write 13, timeout 0
Requests/sec:  14346.72
Transfer/sec:      6.01MB
```

## redis backend

### calculate

```bash
wrk -t8 -c256 -d120s --latency --script=post-calculate.lua http://127.0.0.1:8120/api/v1/formula/calculate
```

```text
Running 2m test @ http://127.0.0.1:8120/api/v1/formula/calculate
  8 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    28.00ms   13.02ms 310.82ms   89.53%
    Req/Sec     1.17k   280.89     2.81k    70.40%
  Latency Distribution
     50%   26.07ms
     75%   30.43ms
     90%   37.47ms
     99%   76.04ms
  1118960 requests in 2.00m, 434.52MB read
  Socket errors: connect 0, read 336, write 2, timeout 0
Requests/sec:   9317.43
Transfer/sec:      3.62MB
```

### convert

```bash
wrk -t8 -c256 -d120s --latency --script=post-convert.lua http://127.0.0.1:8120/api/v1/formula/convert
```

```text
Running 2m test @ http://127.0.0.1:8120/api/v1/formula/convert
  8 threads and 256 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    24.86ms   16.52ms 657.68ms   98.04%
    Req/Sec     1.32k   203.70     1.91k    68.07%
  Latency Distribution
     50%   24.22ms
     75%   27.12ms
     90%   30.81ms
     99%   50.56ms
  1263382 requests in 2.00m, 529.16MB read
  Socket errors: connect 0, read 195, write 2, timeout 0
Requests/sec:  10523.35
Transfer/sec:      4.41MB
```

[wg/wrk](https://github.com/wg/wrk)