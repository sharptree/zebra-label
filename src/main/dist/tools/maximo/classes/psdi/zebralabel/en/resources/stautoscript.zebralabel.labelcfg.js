/* Example Label JSON entry.
  {
    "label": "LABEL_NAME",
    "description": "LABEL_DESCRIPTION",
    "media": "Media Size, typically 2x4, 6x4 etc",
    "usewith": "INVBALANCES, INVENTORY, MATRECTRANS",
    "zpl": "ZPL label definition starting with ^XA and ending with ^XZ",
    "default":"true or false"
  }
*/

var labels = [];

var scriptConfig = {
    "autoscript": "STAUTOSCRIPT.ZEBRALABEL.LABELCFG",
    "description": "Barcode Label Configurations",
    "version": "1.0.0",
    "active": true,
    "logLevel": "ERROR"
};


