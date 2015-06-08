
run_tesseract = {}
run_tesseract = function(imageData) {
    cordova.exec(
        function(results) {
            return results;
        }, 
        function(err) {
            callback('Nothing to echo.');
        }, "Tesseract", "run", [imageData]
    );
};
