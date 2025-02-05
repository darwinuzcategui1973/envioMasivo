<!-- html2xhtml.js written by Jacob Lee <letsgolee@lycos.co.kr> //-->
<!-- Recuerda reemplazar richedit.docXHtml por richedit.value -->
<link href="estilo/rte.css" rel="stylesheet" type="text/css" />
<style>
    .fondoEditor {
        background-color: white;
    }
</style>
<script type="text/javascript" src="./estilo/html2xhtml.js"></script>
<script type="text/javascript" src="./estilo/richtext_compressed.js"></script>
<script type="text/javascript" >
String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
function isEmptyRicheditValue(valor) {
        var s = "<style type=\"text/css\">@import url(./estilo/)\;</style>";
        var cad = valor;
        cad = cad.replace(/<br>/g,"");
        cad = cad.replace(s,"");
        cad = cad.replace(/&nbsp\;/g,"");
        return (cad.trim().length==0);
}
</script>