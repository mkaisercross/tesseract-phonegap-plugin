
/**************************************************************
 * Tesseract Factory function for use as angularJS service
 **************************************************************/

tesseractFactory = function() {
    return {
        run: function(imageData, successFunc, errorFunc) {
            console.log("ReceiptOcr.js: run()");
            this.successFunc = successFunc;
            this.errorFunc = errorFunc;
            this2 = this;
            cordova.exec(
                function(results) {
                    this2.successFunc(results)
                }, 
                function(err) {
                    this2.errorFunc()
                }, "ReceiptOcr", "run", [imageData]
            );
        },
        copyLanguageFiles: function(successFunc, errorFunc) {
            console.log("ReceiptOcr.js: copyLanguageFiles()");
            this.successFunc = successFunc;
            this.errorFunc = errorFunc;
            this2 = this;
            cordova.exec(
                function(wasSuccessful) {
                    this2.successFunc()
                }, 
                function(err) {
                    this2.errorFunc()
                }, "ReceiptOcr", "copyLanguageFiles", []
            );

        }

    };
}



