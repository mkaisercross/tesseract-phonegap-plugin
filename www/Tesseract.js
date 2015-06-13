

tesseractFactory = function() {
    return {
        run: function(data, successFunc) {
            cordova.exec(successFunc,
                function(results) {
                    successFunc(results)
                }, 
                function(err) {
                    errorFunc()
                }, "Tesseract", "run", [imageData]
            );
        },
        successFunc: null,
        errorFunc: null
    };
}



