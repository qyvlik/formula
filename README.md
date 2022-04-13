# formula

Update variable, and use javascript engine calculate the result. 

For example, regularly update the variables `fiat_usd_cny` and `huobipro_usdt_btc`, 
and calculate `huobipro_usdt_btc*fiat_usd_cny`, you can get the bitcoin CNY price.

> **Use the [~~qyvlik/formula-data~~](https://github.com/qyvlik/formula-data) to regularly update the variables.**

## docker

[~~qyvlik/formula~~](https://hub.docker.com/r/qyvlik/formula)

`docker-compose` see [qyvlik/formula-docker](https://github.com/qyvlik/formula-docker) 

## api

### update variables

Update variables, `token` header config key is `formula.access-tokens`, was in `application.yml` file

```bash
curl --location --request POST 'http://127.0.0.1:8120/api/v1/formula/variable/market-price/update' \
--header 'TOKEN: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
--header 'Content-Type: application/json' \
--data-raw '{
    "exchange": "okex",
    "code": "btc_usdt",
    "base": "btc",
    "quote": "usdt",
    "price": "100",
    "timestamp": 0
}'
```

response

```json
{
  "code": 200,
  "data": null,
  "message": null,
  "success": true
}
```

### get variable by name

Get variable by name, the name is a path-variable in http path.

```bash
curl --location --request GET 'http://127.0.0.1:8120/api/v1/formula/variable/market-price/info?exchange=okex&base=btc&quote=usdt' \
--header 'TOKEN: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
--data-raw ''
```

response:

```json
{
  "code": 200,
  "data": {
    "exchange": "okex",
    "code": "btc_usdt",
    "base": "btc",
    "quote": "usdt",
    "price": 100,
    "timestamp": 0
  },
  "message": null,
  "success": true
}
```

### calculate the formula

Calculate the formula, such as `okex_btc_usdt*1.101`.

**Make sure, you have regularly update the `huobipro_usdt_btc`.**

```bash
curl --location --request POST 'http://127.0.0.1:8120/api/v1/formula/calculate' \
--header 'TOKEN: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
--header 'Content-Type: application/json' \
--data-raw '{
    "formula": "okex_btc_usdt*1.101"
}'
```

```json
{
  "code": 200,
  "data": {
    "origin": "okex_btc_usdt*1.101",
    "formula": "okex_btc_usdt*1.101",
    "result": 110.1,
    "variables": [
      {
        "name": "okex_btc_usdt",
        "value": 100,
        "market": {
          "exchange": "okex",
          "code": "btc_usdt",
          "base": "btc",
          "quote": "usdt",
          "price": 100,
          "timestamp": 0
        }
      }
    ]
  },
  "message": null,
  "success": true
}
```

### convert

```bash
curl --location --request POST 'http://127.0.0.1:8120/api/v1/formula/convert' \
--header 'TOKEN: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
--header 'Content-Type: application/json' \
--data-raw '{
    "source": "btc",
    "target": "usdt",
    "amount": "0.99",
    "scale": 18,
    "middles": ["btc", "usdt"],
    "exchanges": ["okex"]
}'
```
 
```json
{
  "code": 200,
  "data": {
    "source": "btc",
    "target": "usdt",
    "result": 99.000000000000000000,
    "price": 100.000000000000000000,
    "amount": 0.99,
    "path": [
      {
        "market": {
          "exchange": "okex",
          "code": "btc_usdt",
          "base": "btc",
          "quote": "usdt",
          "price": 100,
          "timestamp": 0
        },
        "source": "btc",
        "target": "usdt",
        "price": 100
      }
    ]
  },
  "message": null,
  "success": true
}
```
