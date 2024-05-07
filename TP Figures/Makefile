# Executables
OSTYPE = $(shell uname -s)
JAVAC = javac
JAVA = java
A2PS = a2ps-utf8
GHOSTVIEW = gv
DOCP = javadoc
ARCH = zip
PS2PDF = ps2pdf -dPDFX=true -sPAPERSIZE=a4
DATE = $(shell date +%Y-%m-%d)
# Options de compilation
#CFLAGS = -verbose
CFLAGS =
CLASSPATH=.

JAVAOPTIONS = --verbose

PROJECT=Figures
# nom du fichier d'impression
OUTPUT = $(PROJECT)
# nom du répertoire ou se situera la documentation
DOC = doc
# lien vers la doc en ligne du JDK
WEBLINK = "http://docs.oracle.com/javase/8/docs/api/"
# lien vers la doc locale du JDK
LOCALLINK = "file:///Users/davidroussel/Documents/docs/java/api/"
# nom de l'archive
ARCHIVE = $(PROJECT)
# format de l'archive pour la sauvegarde
ARCHFMT = zip
# Répertoire source
SRC = src
# Répertoire bin
BIN = bin
# Répertoire Listings
LISTDIR = listings
# Répertoire Archives
ARCHDIR = archives
# Répertoire Figures
FIGDIR = graphics
# noms des fichiers sources
MAIN = TestFigures RunAllTests
SOURCES = \
$(foreach name, $(MAIN), $(SRC)/$(name).java) \
$(SRC)/points/package-info.java \
$(SRC)/points/Point2D.java \
$(SRC)/points/Vector2D.java \
$(SRC)/points/Point3D.java \
$(SRC)/points/Vector3D.java \
$(SRC)/figures/package-info.java \
$(SRC)/figures/Figure.java \
$(SRC)/figures/AbstractFigure.java \
$(SRC)/figures/Circle.java \
$(SRC)/figures/Rectangle.java \
$(SRC)/figures/Polygon.java \
$(SRC)/figures/Triangle.java \
$(SRC)/figures/Group.java \
$(SRC)/tests/package-info.java \
$(SRC)/tests/junit4/AllTests.java \
$(SRC)/tests/junit4/Point2DTest.java \
$(SRC)/tests/junit4/Point3DTest.java \
$(SRC)/tests/junit4/CircleTest.java \
$(SRC)/tests/junit4/RectangleTest.java \
$(SRC)/tests/junit4/PolygonTest.java \
$(SRC)/tests/junit4/GroupTest.java \
$(SRC)/tests/junit4/FigureTest.java \
$(SRC)/tests/junit5/AllTests.java \
$(SRC)/tests/junit5/Point2DTest.java \
$(SRC)/tests/junit5/Point3DTest.java \
$(SRC)/tests/junit5/CircleTest.java \
$(SRC)/tests/junit5/RectangleTest.java \
$(SRC)/tests/junit5/PolygonTest.java \
$(SRC)/tests/junit5/GroupTest.java \
$(SRC)/tests/junit5/FigureTest.java

OTHER = .project .classpath

.PHONY : doc ps

# Les targets de compilation
# pour générer l'application
all : $(foreach name, $(MAIN), $(BIN)/$(name).class)

#règle de compilation générique
$(BIN)/%.class : $(SRC)/%.java
	$(JAVAC) -sourcepath $(SRC) -classpath $(BIN):$(CLASSPATH) -d $(BIN) $(CFLAGS) $<

# Edition des sources $(EDITOR) doit être une variable d'environnement
edit :
	$(EDITOR) $(SOURCES) Makefile &

# nettoyer le répertoire
clean :
	find bin/ -type f -name "*.class" -exec rm -f {} \;
	rm -rf *~ $(DOC)/* $(LISTDIR)/*

#realclean : clean
#	rm -f $(ARCHDIR)/*.$(ARCHFMT) 

# générer le listing
$(LISTDIR) : 
	mkdir $(LISTDIR)

ps : $(LISTDIR)
	$(A2PS) -2 --file-align=fill --line-numbers=1 --font-size=10 \
	--chars-per-line=100 --tabsize=4 --pretty-print \
	--highlight-level=heavy --prologue="gray" \
	-o$(LISTDIR)/$(OUTPUT).ps Makefile $(SOURCES)

pdf : ps 
	$(PS2PDF) $(LISTDIR)/$(OUTPUT).ps $(LISTDIR)/$(OUTPUT).pdf

# générer le listing lisible pour Gérard
bigps :
	$(A2PS) -1 --file-align=fill --line-numbers=1 --font-size=10 \
	--chars-per-line=100 --tabsize=4 --pretty-print \
	--highlight-level=heavy --prologue="gray" \
	-o$(LISTDIR)/$(OUTPUT).ps Makefile $(SOURCES)

bigpdf : bigps
	$(PS2PDF) $(LISTDIR)/$(OUTPUT).ps $(LISTDIR)/$(OUTPUT).pdf

# voir le listing
preview : ps
	$(GHOSTVIEW) $(LISTDIR)/$(OUTPUT); rm -f $(LISTDIR)/$(OUTPUT) $(LISTDIR)/$(OUTPUT)~

# générer la doc avec javadoc
doc : $(SOURCES)
	$(DOCP) -private -d $(DOC) -author -link $(LOCALLINK) $(SOURCES)
#	$(DOCP) -private -d $(DOC) -author -linkoffline $(WEBLINK) $(LOCALLINK) $(SOURCES)

# générer une archive de sauvegarde
$(ARCHDIR) : 
	mkdir $(ARCHDIR)

archive : pdf $(ARCHDIR)
	$(ARCH) $(ARCHDIR)/$(ARCHIVE)-$(DATE).$(ARCHFMT) $(SOURCES) $(LISTDIR)/*.pdf $(OTHER) $(BIN) Makefile $(FIGDIR)/*.pdf

# exécution des programmes de test
run : all
	$(foreach name, $(MAIN), $(JAVA) -classpath $(BIN):$(CLASSPATH) $(name) $(JAVAOPTIONS) )
