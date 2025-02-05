@echo off
rem Este archivo genera los archivos del webservices
rem Hay que tener instalado JAXWS 2.1.2 y colocar la carpeta bin en el path
rem Se debe ejecutar desde la carpeta classes
rem colocar en el path D:\app\jdk6\bin\
rem crear la carpeta WEB-INF/wsdl y/o borrar su contenido  (si no no compila)
wsgen -cp . -s ../src -d . -verbose -r ..\wsdl -wsdl com.focus.qwds.ondemand.server.documentos.ws.ServicioDocumentoWS
