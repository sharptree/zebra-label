/* Example Printer JSON entry.
  {
    "printer": "PRINTER_NAME",
    "description": "Printer Description",
    "address": "Host name (central-printer.acme.com) or IP (192.168.1.10)",
    "port": 9100,
    "location": "CENTRAL",
    "media": "Media Size, typically 2x4, 6x4 etc",
    "default":"true or false"
    "siteid": "BEDFORD",
    "orgid": "EAGLENA"
  }
*/

var printers = [];

var scriptConfig = {
    "autoscript": "STAUTOSCRIPT.ZEBRALABEL.PRINTERCFG",
    "description": "Barcode Printer Configurations",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR"
};