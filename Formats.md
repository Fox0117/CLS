## Users request format

```
startDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (inclusive)
endDate = "01.02.2018 17:06" // "dd.mm.yyyy HH:MM" (invlusive)
```

## Users response format

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
