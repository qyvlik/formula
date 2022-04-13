wrk.method = "POST"
wrk.body   = '{"source":"btc","target":"usdt","amount":"0.99","scale":18,"middles":["btc","usdt"],"exchanges":["okex"]}'
wrk.headers["Content-Type"] = "application/json"
wrk.headers["TOKEN"] = "ad82c6ae-f7a3-486b-b933-aa19104d8142"
