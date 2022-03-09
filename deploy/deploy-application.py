import sys

execfile('wsadminlib.py')

filePath, appName, virtualHost, cellName, nodeName, serverName, webserverName = sys.argv[:7]

def updateModuleApp():

    moduleList = "+WebSphere:cell=" + cellName + ",server=" + serverName + \
        "+WebSphere:cell=" + cellName + ",node=" + nodeName + ",server=" + webserverName
    print "Updating module mapping with " + moduleList
    AdminApp.edit(appName, ['-MapModulesToServers',[['.*', '.*', moduleList]]])
    saveAndSync()

apps = AdminApp.list("WebSphere:cell="+cellName +",node="+ nodeName + ",server=" + serverName).split('\n')

if appName in apps:
   print "Updating the application."
   AdminApp.update(appName, 'app', ['-operation', 'update', '-contents', filePath,],)
   saveAndSync()
else:
   print "Installing the application."
   AdminApp.install(filePath,['-distributeApp','-usedefaultbindings','-cell',cellName,'-node',nodeName, '-server',serverName,'-appname', appName, '-MapWebModToVH',[['.*','.*',virtualHost]]])
   saveAndSync()

updateModuleApp()
