## Users range request

```
no parameters
```

## Users range response

```javascript
{
    "status": "OK", // or "Error"
    "error_message": "Some message", // only if status == Error
    "minimum_date": "01.02.2018 17:06", // "dd.mm.yyyy HH:MM" (inclusive) // returns minimum date for user requests
    "maximum_date": "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive) // returns maximum date for user requests
}
```

## Users request

```
startDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive)
endDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive)
```

## Users response

```javascript
{
    "status": "OK", // or "Error"
    "error_message": "Some message", // only if status == Error
    "items": [
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
    "status": "OK", // or "Error"
    "error_message": "Some message", // only if status == Error
    "text": "function () { return \"Test message\" }" // current script
}
```

## JS upload script request

```
scriptText = "function () { return \"Test message\" }" // script text
```
