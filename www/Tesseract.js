

tesseractFactory = function() {
    return {
        run: function(imageData, successFunc, errorFunc) {
            this.successFunc = successFunc;
            this.errorFunc = errorFunc;
            cordova.exec(
                function(results) {
                    successFunc(results)
                }, 
                function(err) {
                    errorFunc()
                }, "Tesseract", "run", [imageData]
            );
        }
    };
}



