# Skikoのバグにより起動できない事象の回避
以下のように`renderApi`を`SOFTWARE`に変更してください。
```diff
{

   "areaSize": {
        "first": 0.8,
        "second": 0.8
    },
    "areaOffset": {
        "first": 0.0,
        "second": 0.0
    },
    "alwaysTop": true,
    "imageSize": 175.0,
    "spawnCount": 1,
    "graphics": {
        "vsync": true,
        "fps": false,
-       "renderApi": "",
+       "renderApi": "SOFTWARE",
        "gpu": "Auto"
    },
    "debug": {
        "enable": false
    }
}
```
