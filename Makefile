all: Main.class

Main.class: Main.java Game.java
	javac Main.java
	
	

.PHONY: clear run jar
	
jar: Main.class
	jar cfm gameoflife.jar Manifest.txt *.class

clear:
	rm -f *.class
	rm -f *~

run:
	java Main
	
	
