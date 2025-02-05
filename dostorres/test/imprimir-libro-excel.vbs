'imprimir-libro-excel.vbs
'
'Script VBScript que permite imprimir
'un libro Excel completo
'
'Sintaxis
'
'cscript [//nologo] imprimir-libro-excel.vbs
'          /L:libro [/D:desde] [/H:hasta]
'          [/C:n� copias] [/V] [P:impresora]
'          [/A] [/N:nombre archivo]] [/I] [/?]
'
'Siendo
'
'- /L: requerido. Ruta y nombre del libro Excel
'                 a imprimir
'- /D: opcional.  P�gina a partir de la cual se
'                 empieza a imprimir. Si se omite
'                 se empieza con la primera. No
'                 confundir con hojas del libro,
'                 son las p�ginas de impresi�n.
'- /H: opcional.  P�gina a hasta la cual se imprime
'                 Si se omite se imprime hasta la
'                 �ltima. No confundir con hojas
'                 del libro, son las p�ginas de
'                 impresi�n.
'- /C: opcional.  N�mero de copias a imprimir. Si
'                 se omite se imprimir� una
'- /V: opcional.  Si se pasa este modificador se
'                 mostrar� la vista previa.
'- /P: opcional.  Impresora por la que se imprimira.
'                 Si se omite ser� la impresora
'                 predeterminada
'- /A: opcional.  Si se pasa, se imprimir� a un
'                 archivo. Se imprimir� en el fichero
'                 especificado por el par�metro /A,
'                 o, en caso de no ser pasado, se
'                 pedir� al usuario que especifique
'                 el nombre del fichero
'- /N: opcional.  Si se establece el modificador /A,
'                 este par�metro recibe el nombre
'                 del fichero al que se volcar� la
'                 impresi�n.
'- /I: opcional.  Si se establece este modificador,
'                 se intercalar�n las p�ginas.
'- /?: opciona.   Muestra la ayuda en l�nea.
'
'Ejemplos
'
'- Imprime completo el libro
'  c:morialistado de orcos.xls
'  por la impresora predeterminada
'
'cscript //nologo imprimir-libro-excel.vbs
'        /L:"c:morialistado de orcos.xls"

'
'- Imprime el libro
'  c:morialistado de orcos.xls
'  por la impresora predeterminada de la
'  p�gina 4 hasta la 6
'
'cscript //nologo imprimir-libro-excel.vbs
'        /L:"c:morialistado de orcos.xls"
'      /D:4 /H:6 /P:"\smoriaprinter001"
'
'- Imprime completo el libro
'  c:morialistado de orcos.xls
'  por la impresora predeterminada, mostrando
'  la vista previa
'
'cscript //nologo imprimir-libro-excel.vbs
'        /L:"c:morialistado de orcos.xls"
'        /V
'- Imprime completo el libro
'  c:morialistado de orcos.xls
'  al fichero
'  c:morialistado de orcos.prn
'
'cscript //nologo imprimir-libro-excel.vbs
'        /L:"c:morialistado de orcos.xls"
'      /A /N:"c:morialistado de orcos.prn"
'
'� Fernando Reyes - Junio de 2007

Dim app_Excel 'As Excel.Application
Dim wb_Libro 'As Excel.Workbook
Dim str_Libro 'As String
Dim int_Desde 'As Integer
Dim int_Hasta 'As Integer
Dim int_Copias 'As Integer
Dim bol_VistaPrevia 'As Boolean
Dim str_Impresora 'As String
Dim bol_ImprimirEnArchivo 'As Boolean
Dim bol_Intercalar 'As Boolean
Dim str_NombreArchivo 'As String
Dim int_Devolucion 'As Integer

'Si se ha pedido la ayuda..
If WScript.Arguments.Named.Exists("?") Then

    'La mostramos y salimos del script
    Call s_Ayuda("*********************" & _
                 vbCrLf & _
                 "*       AYUDA       *" & _
                 vbCrLf & _
                 "*********************")

    WScript.Quit 0

End If

int_Devolucion = 0

'Recogemos el �nico par�metro requerido,
'el nombre del libro a imprimir
If WScript.Arguments.Named.Exists("L") Then

    str_Libro = WScript.Arguments.Named("L")

Else

    'Si no se ha recibido, advertimos que es
    'un par�metro requerido, mostramos la ayuda
    'y terminamos el script con devoluci�n 1
    Call s_Ayuda("Error 1: No se ha pasado" & _
                 " el nombre del libro a " & _
                 "imprimir.")

    WScript.Quit 1

End If

'Recogemos los par�metros y modificadores
'opcionales

'Primero el inicio de impresi�n, si no se pasa
'no se le pasar� al m�todo PrintOut del libro
'con lo que la impresi�n empieza desde el
'principio
If WScript.Arguments.Named.Exists("D") Then

    int_Desde = WScript.Arguments.Named("D")

Else

    int_Desde = Null

End If

'Ahora el final de impresion; al igual que con
'el principio, si no se especifica se imprimir�
'hasta la �ltima hoja
If WScript.Arguments.Named.Exists("H") Then

    int_Hasta = WScript.Arguments.Named("H")

Else

    int_Hasta = Null

End If

'Ahora el n�mero de copias, si no se pasa se
'imprimir� una
If WScript.Arguments.Named.Exists("C") Then

    int_Copias = WScript.Arguments.Named("C")

Else

    int_Copias = 1

End If

'Ahora recibimos si se mostrar� la vista previa
'o no
If WScript.Arguments.Named.Exists("V") Then

    bol_VistaPrevia = True

Else

    bol_VistaPrevia = False

End If

'Miramos si se ha recibido la impresora. En caso
'contrario, se imprimir� por la predeterminada
If WScript.Arguments.Named.Exists("P") Then

    str_Impresora = WScript.Arguments.Named("P")

Else

    str_Impresora = Null

End If

'Le toca el turno a la opci�n de imprimir en un
'fichero
If WScript.Arguments.Named.Exists("A") Then

    bol_ImprimirEnArchivo = True

    'En el caso de que se tenga que imprimir en archivo
    'miramos si se ha recibido el nombre del archivo.
    'En caso contrario, se le pedir� al usuario que lo
    'especifique
    If WScript.Arguments.Named.Exists("N") Then

        str_NombreArchivo = WScript.Arguments.Named("N")

    Else

        str_NombreArchivo = Null

    End If

Else

    bol_ImprimirEnArchivo = Null
    str_NombreArchivo = Null

End If

'Ahora veremos si hay que intercalar o no las hojas
If WScript.Arguments.Named.Exists("I") Then

    bol_Intercalar = True

Else

    bol_Intercalar = False

End If

'Creamos un objeto aplicaci�n de Excel
Set app_Excel = CreateObject("Excel.Application")
'Hacemos visible Excel
app_Excel.Visible = False

'Abrimos el libro
Set wb_Libro = app_Excel.Workbooks.Open(str_Libro)


WScript.Echo "Impresora " & str_Impresora
WScript.Echo "Copias " & int_Copias

'Establecemos control de errores
On Error Resume Next

wb_Libro.ActivePrinter str_Impresora
wb_Libro.PrintOut int_Desde, int_Hasta, int_Copias, bol_VistaPrevia

'wb_Libro.PrintOut int_Desde, int_Hasta, int_Copias, bol_VistaPrevia, str_Impresora, bol_ImprimirEnArchivo, bol_Intercalar, str_NombreArchivo
'wb_Libro.PrintOut null, null, 2, false, "\\ccsfocus01\Xerox Printer"


                  
                  
'WScript.Echo "Parametros"
'WScript.Echo int_Hasta
'WScript.Echo int_Copias
'WScript.Echo bol_VistaPrevia
'WScript.Echo str_Impresora
'WScript.Echo bol_ImprimirEnArchivo
'WScript.Echo bol_Intercalar
'WScript.Echo str_NombreArchivo
                  

'si se ha producido error, lo mostramos y recogemos
'su n�mero para ponerlo como devoluci�n del script
If Err.Number <> 0 Then

    WScript.Echo "Error " & Err.Number & _
                 ": " & Err.Description

    int_Devolucion = Err.Number

End If

'Cerramos el libro
wb_Libro.Close
'Cerramos Excel
app_Excel.Quit

'Nos limpiamos la parte posterior
'saliente :-))
Set wb_Libro = Nothing
Set app_Excel = Nothing

'Terminamos el script con la
'devoluci�n que corresponda: cero
'si todo ha ido bien o el n�mero de
'error que se haya producido
WScript.Quit int_Devolucion

Sub s_Ayuda(str_Error)

    If Len(str_Error) > 0 Then

        WScript.Echo str_Error & _
                     vbCrLf & vbCrLf

    End If

    WScript.Echo "imprimir-libro-excel.vbs"
    WScript.Echo ""
    WScript.Echo "Script VBScript que perm" & _
                 "ite imprimir"
    WScript.Echo "un libro Excel completo"
    WScript.Echo ""
    WScript.Echo "Sintaxis"
    WScript.Echo ""
    WScript.Echo "cscript [//nologo] impri" & _
                 "mir-libro-excel.vbs /L:" & _
                 "libro [/D:desde] [/H:ha" & _
                 "sta] [/C:n� copias] [/V]" & _
                 " [P:impresora] [/A] [/N:n" & _
                 "ombre archivo]] [/I] [/?]"
    WScript.Echo ""
    WScript.Echo "Siendo"
    WScript.Echo ""
    WScript.Echo "- /L: requerido. Ruta y " & _
                 "nombre del libro Excel"
    WScript.Echo "                 a impri" & _
                 "mir"
    WScript.Echo "- /D: opcional.  P�gina " & _
                 "a partir de la cual se"
    WScript.Echo "                 empieza" & _
                 " a imprimir. Si se omite"
    WScript.Echo "                 se empi" & _
                 "eza con la primera. No"
    WScript.Echo "                 confund" & _
                 "ir con hojas del libro,"
    WScript.Echo "                 son las" & _
                 " p�ginas de impresi�n."
    WScript.Echo "- /H: opcional.  P�gina " & _
                 "a hasta la cual se impri" & _
                 "me"
    WScript.Echo "                 Si se o" & _
                 "mite se imprime hasta la "
    WScript.Echo "                 primera" & _
                 "�ltima . No confundir con hojas"
    WScript.Echo "                 del lib" & _
                 "ro, son las p�ginas de"
    WScript.Echo "                 impresi" & _
                 "�n."
    WScript.Echo "- /C: opcional.  N�mero " & _
                 "de copias a imprimir. Si"
    WScript.Echo "                 se omit" & _
                 "e se imprimir� una"
    WScript.Echo "- /V: opcional.  Si se p" & _
                 "asa este modificador se"
    WScript.Echo "                 mostrar" & _
                 "� la vista previa."
    WScript.Echo "- /P: opcional.  Impreso" & _
                 "ra por la que se imprimi" & _
                 "ra."
    WScript.Echo "                 Si se o" & _
                 "mite ser� la impresora"
    WScript.Echo "                 predete" & _
                 "rminada"
    WScript.Echo "- /A: opcional.  Si se p" & _
                 "asa, se imprimir� a un"
    WScript.Echo "                 archivo" & _
                 ". Se imprimir� en el fic" & _
                 "hero"
    WScript.Echo "                 especif" & _
                 "icado por el par�metro /" & _
                 "A,"
    WScript.Echo "                 o, en ca" & _
                 "so de no ser pasado, se"
    WScript.Echo "                 pedir� " & _
                 "al usuario que especifiq" & _
                 "ue"
    WScript.Echo "                 el nomb" & _
                 "re del fichero"
    WScript.Echo "- /N: opcional.  Si se e" & _
                 "stablece el modificador " & _
                 "/A,"
    WScript.Echo "                 este pa" & _
                 "r�metro recibe el nombre"
    WScript.Echo "                 del fic" & _
                 "hero al que se volcar� l" & _
                 "a"
    WScript.Echo "                 impresi" & _
                 "�n."
    WScript.Echo "- /I: opcional.  Si se e" & _
                 "stablece este modificado" & _
                 "r,"
    WScript.Echo "                 se inte" & _
                 "rcalar�n las p�ginas."
    WScript.Echo "- /?: opcional.  Muestra" & _
                 " la ayuda en l�nea."
    WScript.Echo ""
    WScript.Echo "Ejemplos"
    WScript.Echo ""
    WScript.Echo "- Imprime completo el li" & _
                 "bro c:morialistado de " & _
                 "orcos.xls por la impreso" & _
                 "ra predeterminada"
    WScript.Echo ""
    WScript.Echo "cscript //nologo imprimi" & _
                 "r-libro-excel.vbs /L:""c" & _
                 ":morialistado de orcos" & _
                 ".xls"""
    WScript.Echo ""
    WScript.Echo "- Imprime el libro c:mo" & _
                 "rialistado de orcos.xls" & _
                 " por la impresora predet" & _
                 "erminada de la p�gina 4 " & _
                 "hasta la 6"
    WScript.Echo ""
    WScript.Echo "cscript //nologo imprimi" & _
                 "r-libro-excel.vbs /L:""c" & _
                 ":morialistado de orcos" & _
                 ".xls"" /D:4 /H:6 /P:""\" & _
                 "smoriaprinter001"""
    WScript.Echo ""
    WScript.Echo "- Imprime completo el li" & _
                 "bro c:morialistado de " & _
                 "orcos.xls por la impreso" & _
                 "ra predeterminada, mostr" & _
                 "ando la vista previa"
    WScript.Echo ""
    WScript.Echo "cscript //nologo imprimi" & _
                 "r-libro-excel.vbs /L:""c" & _
                 ":morialistado de orcos" & _
                 ".xls"" /V"
    WScript.Echo "- Imprime completo el li" & _
                 "bro c:morialistado de " & _
                 "orcos.xls al fichero c:" & _
                 "morialistado de orcos.prn"
    WScript.Echo ""
    WScript.Echo "cscript //nologo imprimi" & _
                 "r-libro-excel.vbs /L:""c" & _
                 ":morialistado de orcos" & _
                 ".xls"" /A /N:""c:moria" & _
                 "listado de orcos.prn"""

End Sub