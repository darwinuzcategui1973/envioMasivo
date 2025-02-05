#include <windows.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <io.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/types.h>
//#include <string>
//#include <vector>

//using namespace std;

char *replace_str(char *str, char *orig, char *rep)
{
  static char buffer[4096];
  char *p;

  if(!(p = strstr(str, orig)))  // Is 'orig' even in 'str'?
    return str;

  strncpy(buffer, str, p-str); // Copy characters from 'str' start to 'orig' st$
  buffer[p-str] = '\0';

  sprintf(buffer+(p-str), "%s%s", rep, p+strlen(orig));

  return buffer;
}

int main(int argc, char* argv[])
{
	char driveDoc[_MAX_DRIVE];
	char dirDoc[_MAX_DIR];
	char fnameDoc[_MAX_FNAME];
	char extDoc[_MAX_EXT];

	char drive[_MAX_DRIVE];
	char dir[_MAX_DIR];
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];

	char *m_ptrDocFile;
	char *m_ptrOutputFile;
	char *m_ptrOrientation;
	char *m_ptrWidthHeight;

	DEVMODE nDevMode;

	int r = 0;
	int iRet = 0;
	int iStartTick = 0;
	int iEndTick = 0;

	char parametro[80];

	LoadLibrary("doc2img.dll"); // funcionando

	if(argc != 5)
	{
		printf("Usage:\n");
		printf("\"%s\" \"C:\\input.doc\" \"C:\\input.pdf\" H 0\n",argv[0]);
		printf("\"%s\" \"C:\\input.doc\" \"C:\\input.tif\" V 1\n",argv[0]);
		printf("\"%s\" \"C:\\input.doc\" \"C:\\input.jpg\" H 0\n",argv[0]);
		printf("\"%s\" \"C:\\input.doc\" \"C:\\input.bmp\" V 1\n",argv[0]);
		printf("\"%s\" \"C:\\input.doc\" \"C:\\input.emf\" H 0\n",argv[0]);
		return 0;
	}
	m_ptrDocFile = argv[1];
	m_ptrOutputFile = argv[2];
	m_ptrOrientation = argv[3];
	m_ptrWidthHeight = argv[4];


	memset(&nDevMode,0,sizeof(nDevMode));
	nDevMode.dmFields = DM_PRINTQUALITY|DM_YRESOLUTION;
	nDevMode.dmPrintQuality = 100;
	nDevMode.dmYResolution = 100;
	nDevMode.dmColor = DMCOLOR_MONOCHROME;
	docPrint_ChangePrinterSettings("docPrint",&nDevMode);

	//docPrint_Register("Your License Key","Your Company Name");
	//--r = docPrint_Register("VL6IEN2GRW8EYN8P1D7F","Focus Consulting C.A.");  // docprint 4
	//r = docPrint_Register("7550QK5PLSSOKE53","Focus Systems Consulting Corp");  // docprint 7
	//r = docPrint_Register("4650RZICBKRDHO46","Focus Systems Consulting Corp");  // docprint 7
	//r = docPrint_Register("7550QK5PLSSOKE53","Focus Systems Consulting Corp");  // docprint 7


	//	docPrint_SetOptions("m_bCreateFileForEachPage","1");
	//	docPrint_SetOptions("m_strColorDepth","24");
	//	docPrint_SetOptions("m_strResolution","200x200");
	//	docPrint_SetOptions("m_bGrayscale","0");
	//	//Use run length compression arithmetic for TIFF file
	//	docPrint_SetOptions(".tif","-compress rle");
	//	//docPrint_SetOptions(".tif","-compress lzw");
	//	docPrint_SetOptions(".jpg","-quality 100");

	_splitpath(m_ptrDocFile,driveDoc,dirDoc,fnameDoc,extDoc);

	_splitpath(m_ptrOutputFile,drive,dir,fname,ext);

	iStartTick = GetTickCount();

	//iRet = docPrint_DocumentConverter("C:\\wwwroot\\test.jpg","C:\\wwwroot\\test.pdf");
	if(!stricmp(ext,".pdf") || !stricmp(ext,".ps") || !stricmp(ext,".eps"))
		if(!stricmp(m_ptrOrientation,"H")) {
			printf("Horizontal\n");
			printf(extDoc);
			printf(" - ");
			if(!stricmp(extDoc,".doc") || !stricmp(extDoc,".docx")) {
				printf("word\n");
				//--iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, " -O 2");
			} else {
				printf("otro formato\n");
				//--iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, "-f \"215.9x279.4mm\" -O 2");
			}
		} else {
			printf("Vertical\n");
			printf(extDoc);

			if(!stricmp(extDoc,".doc")) {
				printf("word\n");

				if (stricmp(m_ptrWidthHeight,"0")) {
					strcpy (parametro, "-f ");
					strcat (parametro, m_ptrWidthHeight );
				} else {
					strcpy (parametro, " ");
				}
				printf(parametro);
				printf("\n");

				//--iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, parametro); //"-e" convierte con docprint (queda mas pesado)
			} else  {
				printf("otro formato\n");
				//--iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, "-f \"215.9x279.4mm\"");
			}

		}
		//iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, "-f 8"); //NULL); //"-f 8"); hoja estandar
	else
		//--iRet = docPrint_DocumentConverter(m_ptrDocFile,m_ptrOutputFile, "-e");

	iEndTick = GetTickCount();

	printf("%s ==> %s : %s, time = %dms\n",m_ptrDocFile,m_ptrOutputFile,iRet==0?"Success":"Fail.",
		iEndTick-iStartTick);
	return 1;
}
