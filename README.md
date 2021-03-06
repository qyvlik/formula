# formula

Update variable, and use javascript engine calculate the result. 

For example, regularly update the variables `usd_in_cny` and `huobipro_usdt_btc`, 
and calculate `huobipro_usdt_btc*usd_in_cny`, you can get the bitcoin CNY price.

> **Use the [qyvlik/formula-data](https://github.com/qyvlik/formula-data) to regularly update the variables.**

## docker

[qyvlik/formula](https://hub.docker.com/r/qyvlik/formula)

`docker-compose` see [qyvlik/formula-docker](https://github.com/qyvlik/formula-docker) 

## api

### update variables

Update variables, `token` header config key is `formula.access-tokens`, was in `application.yml` file

```bash
curl -X POST \
  http://localhost:8120/api/v1/formula/variables/update \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Content-Length: 131' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache' \
  -H 'token: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
  -d '{
	"variables": [
		{
			"name": "usd_in_cny",
			"value": "7.1291",
			"timestamp": 1568008800000,
			"timeout": 14400000
		}
	]
}'
```

response

```json
{
  "result": "success"
}
```

### delete variables

Delete variables, `token` header config key is `formula.access-tokens`, was in `application.yml` file.

```bash
curl -X POST \
  http://localhost:8120/api/v1/formula/variables/delete \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Content-Length: 50' \
  -H 'Content-Type: application/json' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache' \
  -H 'token: ad82c6ae-f7a3-486b-b933-aa19104d8142' \
  -d '{
	"variableNames": ["usd_in_cny", "usd_in_eur"]
}'
```

response

```json
{
  "result": "success"
}
```

### list all variable names

List all variable names which you save by `update variables` api.

```bash
curl -X GET \
  http://localhost:8120/api/v1/formula/variables/names \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache'
```

response

```json
{
  "result": [
    "usd_in_btc",
    "usd_in_eur"
  ]
}
```

### get variable by name

Get variable by name, the name is a path-variable in http path.

```bash
curl -X GET \
  http://localhost:8120/api/v1/formula/variable/usd_in_krw \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache'
```

response:

```json
{
  "result": {
    "name": "usd_in_krw",
    "value": 1191.57,
    "timestamp": 1568008800000,
    "timeout": 14400000
  }
}
```

### calculate the formula

Calculate the formula, such as `huobipro_usdt_btc*usd_in_cny`.

**Make sure, you have regularly update the `huobipro_usdt_btc` and `usd_in_cny`.**

```bash
curl -X GET \
  'http://localhost:8120/api/v1/formula/eval?formula=huobipro_usdt_btc%2Ausd_in_cny' \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache'
```

```json
{
  "result": "72318.731056"
}
```

Or use `debug` for the formula result detail.


```bash
curl -X GET \
  'http://localhost:8120/api/v1/formula/debug?formula=huobipro_usdt_btc%2Ausd_in_cny' \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache'
```

response

```json
{
  "result": {
    "cost": 15,
    "formula": "huobipro_usdt_btc*usd_in_cny",
    "result": "72318.731056",
    "context": {
      "huobipro_usdt_btc": {
        "name": "huobipro_usdt_btc",
        "value": 10144.16,
        "timestamp": 1568008800000,
        "timeout": 14400000
      },
      "usd_in_cny": {
        "name": "usd_in_cny",
        "value": 7.1291,
        "timestamp": 1568008800000,
        "timeout": 14400000
      }
    }
  }
}
```

### convert

```bash
curl -X GET \
  'http://localhost:8120/api/v1/formula/convert?from=HT&to=OKB&value=1' \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Host: localhost:8120' \
  -H 'User-Agent: PostmanRuntime/7.16.3' \
  -H 'cache-control: no-cache'
```

**Make sure, you have regularly update the `huobipro_ht_usdt` and `okex_okb_usdt`.**

Convert 1 HT to `x` OKB, response as follow:
 
```json
{
  "result": {
    "cost": 11,
    "ts": 1590500103480,
    "formula": "1 * huobipro_ht_usdt / okex_okb_usdt",
    "result": "0.779126213592",
    "context": {
      "okex_okb_usdt": {
        "name": "okex_okb_usdt",
        "value": 4.944,
        "timestamp": 1590500103480,
        "timeout": 300000
      },
      "huobipro_ht_usdt": {
        "name": "huobipro_ht_usdt",
        "value": 3.852,
        "timestamp": 1590500101730,
        "timeout": 300000
      }
    }
  }
}
```

## Nashorn secure

[how-stop-nashorn-from-allowing-the-quit-function](https://stackoverflow.com/questions/31127641/how-stop-nashorn-from-allowing-the-quit-function)

[Nashorn script security permissions](https://wiki.openjdk.java.net/display/Nashorn/Nashorn+script+security+permissions)

[secure-nashorn-js-execution](https://stackoverflow.com/questions/20793089/secure-nashorn-js-execution)
