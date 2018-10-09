
/*

Very simple vacuum cleaner agent in a world that has only four locations.

Perceptions:
. dirty: the current location has dirty
. clean: the current location is clean
. pos(X): the agent position is X (0 < X < 5).

Actions:
. suck: clean the current location
. left, right, up, down: move the agent

*/

 
/* o agente nao sabe o tamanho da sala (quantas posicoes). 
   Em uma arquitetura puramente reativa, sem memoria, nao eh possivel faze-lo
   andar no sentido horario. A solucao eh fazer movimentos aleatorios.
   
   O tamanho do ambiente foi aumentado para 4x4
*/

 
!clean. // initial goal
 // vou fazer uma gente novo


+!clean : clean <- .my_name(N);.print(" Contexto CLEAN ");!move;!clean.
+!clean : dirty <- suck;.my_name(N);.print(" Contexto Dirty "); !move; !clean.
-!clean   <- .wait(2000);.print(" Contexto remove ");!clean.

/* 
!limpar.
!limpo.


+!limpo : clean <- .print(" Limpo ");.wait(2000);!move;!limpo.
*/

/* 
+dirty: true <- .print(" Percebecao Dirty ");!limpar.
+clean: true <- .print(" Percebecao Limpo ");!move.

+!limpar: true <- suck;.print(" Limpando ");.wait(2000).
*/

//+!slow : true <- .wait(2000); !clean.

+!move : true <- .wait(2000);
   .nth(math.random(4), [left, right, up, down], Ac);
   .my_name(N);
   .print(N, " doing ", Ac);
   Ac.

