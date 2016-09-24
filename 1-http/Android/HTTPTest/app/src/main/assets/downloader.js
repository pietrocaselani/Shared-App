var downloader = {
    executeTest: function (name) {
        var message = "Recebi " + name;

        delegate.callback(message);
    },

    download: function (url) {
        if (url) {
            http.get(url, function (response) {
                console.log("Chegou no JS callback!!!");
                var statusCode = response["statusCode"];

                console.log("StatusCode = " + statusCode);

                if (statusCode == 200) {
                    var body = response["body"];

                    console.log("Body = " + body);

                    var jsonResult = JSON.parse(body);

                    console.log("jsonResult = " + jsonResult);

                    var result = {"result": jsonResult};

                    console.log("result = " + result);

                    delegate.onSuccess(result);
                } else {
                    delegate.onError("Error Status Code = " + statusCode);
                }
            });
        } else {
            delegate.onError("Error Invalid URL");
        }
    }
};
