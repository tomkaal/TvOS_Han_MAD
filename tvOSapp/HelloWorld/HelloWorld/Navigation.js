var baseURL;

function loadingTemplate() {
    var template = "<document><loadingTemplate><activityIndicator><text>Loading</text></activityIndicator></loadingTemplate></document>";
    var templateParser = new DOMParser();
    var parsedTemplate = templateParser.parseFromString(template, "application/xml");
    return parsedTemplate;
}

function getDocument(extension) {
    var templateXHR = new XMLHttpRequest();
    var url = baseURL + extension;
    var loadingScreen = loadingTemplate();
    
    templateXHR.responseType = "document";
    templateXHR.addEventListener("load", function() {pushPage(templateXHR.responseXML, loadingScreen);}, false);
    templateXHR.open("GET", url, true);
    templateXHR.send();
}

function pushPage(page, loading) {
    var currentDoc = getActiveDocument();
    navigationDocument.replaceDocument(page, loading);
}

App.onLaunch = function(options) {
    baseURL = options.BASEURL;
    var extension = "templates/InitialPage.xml";
    getDocument(extension);
}
