
JAVAS = ${shell find . -name "*.java"}

bin: $(JAVAS) src/lex/Scanner.java
	mkdir -p bin
	javac -d bin -cp lib/gearley.jar $(JAVAS)
	touch bin

src/lex/Scanner.java: src/lex/mini.lex
	jflex --nobak $?
