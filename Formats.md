## All requests use POST method

## Response codes

| code  |      meaning      |
| :---: | :---------------: |
|  200  |        OK         |
|  4xx  | client side error |
|  5xx  | server side error |
| other |   unknown error   |

## Entries range request

```
no parameters
```

## Entries range response

```javascript
{
    "error_message": "Some message", // optional
    "minimum_date": "01.02.2018 17:06", // "dd.mm.yyyy HH:MM" (inclusive) // returns minimum date for user requests
    "maximum_date": "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive) // returns maximum date for user requests
}
```

## Entries request

```
startDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive)
endDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive)
```

## Entries response

```javascript
{
    "error_message": "Some message", // optional
    "entries": [
        {
            "identifier": "1454Xgf32", // some unique string
            "date": "01.02.2018 17:06" // "dd.mm.yyyy HH:MM"
        },
        {
            ...
        }
    ]
}
```

## JS download script request

```
no parameters
```

## JS download script response

```javascript
{
    "error_message": "Some message", // optional
    "script": "function () { return \"Test message\" }" // current script
}
```

## JS upload script request

```
javascriptStatus = "upload"

body:
    "function () { return \"Test message\" }" // script text
```
