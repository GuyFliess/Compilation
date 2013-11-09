
JAVAS = ${shell find . -name "*.java"}

bin: $(JAVAS) src/lex/Scanner.java
	mkdir -p bin
	javac -d bin $(JAVAS)
	touch bin

src/lex/Scanner.java: src/lex/mini.lex
	jflex --nobak $?
