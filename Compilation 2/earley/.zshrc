prompt='%~§ '
export PATH=/opt/local/bin:$PATH:/Developer/SDKs/android-sdk-macosx/platform-tools
export PATH=$PATH:/usr/local/CrossPack-AVR-20100115/bin
export PATH=$PATH:$HOME/var/scripts
export MANPATH=/opt/local/share/man:$MANPATH
export HISTFILE=~/.zsh_history
export HISTSIZE=50000
export SAVEHIST=50000

export LC_ALL=en_US.utf-8

export ANDROID_HOME=/Developer/SDKs/android-sdk-macosx

export COQ=/usr/local
export COQTOP=$COQ/lib/
export COQLIB=$COQ/lib/
export COQBIN=$COQ/bin/
export SSRCOQ_LIB=$COQLIB/coq/user-contrib/Ssreflect
export PATH=$PATH:$COQBIN

export EDITOR=vim
bindkey -e
bindkey "^[[A" history-search-backward
bindkey "^[[B" history-search-forward

export FAN_HOME=/opt/local/share/java/fantom
export FAN_ENV=util::PathEnv
export FAN_ENV_PATH=~/.fan/

export GRAILS_HOME=/opt/local/share/java/grails
export GRADLE_HOME=/opt/local/share/java/gradle

alias top='\top -o cpu'

# Who the hacker
export WS=$HOME/var/workspace
pypush() { export PYTHONPATH=$1:$PYTHONPATH }
sindarin() { pypush $WS/Sindarin.Fundament/src; 
             pypush $WS/Sindarin.Architecture/src }

### Added by the Heroku Toolbelt
export PATH="/usr/local/heroku/bin:$PATH"



## Switch versions of the source files
1() { git checkout earley-v1 src && rm -rf src/ast }
2() { git checkout earley-v2 src && rm -rf src/ast }
3() { git checkout earley-v3 src }
4() { git checkout earley-v4 src }


c() { rm -rf bin src/lex/Scanner.java }
