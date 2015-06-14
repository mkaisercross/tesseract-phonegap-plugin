
/**************************************************************
 * Tesseract Factory function for use as angularJS service
 **************************************************************/

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
                }, "ReceiptOcr", "run", [imageData]
            );
        }
    };
}



