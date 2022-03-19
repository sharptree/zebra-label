var printers = [{
    "name": "desktop",
    "description": "Desktop Zebra Printer",
    "address": "73.169.185.240",
    "port": 9100,
    "storeroom": "CENTRAL",
    "siteid": "BEDFORD",
    "media": "3x4",
    "default": true
},
{
    "name": "desktop",
    "description": "Desktop Zebra Printer",
    "address": "73.169.185.240",
    "port": 9100,
    "storeroom": "PKG",
    "siteid": "BEDFORD",
    "media": "3x4",
    "default": true
},
{
    "name": "storeroom",
    "description": "Storeroom Printer",
    "address": "192.168.1.173",
    "port": 9100
}
];

main();

function main() {




    if (typeof request !== 'undefined' && request) {
        responseBody = JSON.stringify(printers);
    }
}

var scriptConfig = {
    "autoscript": "STAUTOSCRIPT.PRINTERS",
    "description": "Barcode Printers",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR"
};