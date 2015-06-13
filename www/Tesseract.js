

tesseractFactory = function() {
    return {
        run: function(data) {
            cordova.exec(
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



