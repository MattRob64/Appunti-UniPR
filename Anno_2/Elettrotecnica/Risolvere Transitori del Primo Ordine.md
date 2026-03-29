# Risolvere Transitori del Primo Ordine in Elettrotecnica
I problemi di transitorio in elettrotecnica possono sembrare spaventosi perché introducono il concetto di "tempo" in circuiti che prima analizzavi solo in condizioni statiche (a regime). Spesso i libri di testo partono subito con le equazioni differenziali, ma nella pratica esiste un metodo molto più semplice e meccanico per risolvere i circuiti del primo ordine (quelli con resistenze e un solo condensatore o un solo induttore).

## 1. I concetti fondamentali: Le "Memorie" del circuito
Prima di fare i calcoli, devi ricordare due regole d'oro (il principio di continuità):
- Il condensatore (C) ha un'inerzia sulla tensione. La tensione ai suoi capi non può cambiare istantaneamente. Se un istante prima valeva 5V, l'istante dopo varrà ancora 5V.
- L'induttore (L) ha un'inerzia sulla corrente. La corrente che lo attraversa non può variare istantaneamente.

Il transitorio è semplicemente la fase di passaggio tra una situazione di "calma" (regime) passata e una nuova situazione di "calma" futura, scatenata da un evento (es. l'apertura o chiusura di un interruttore al tempo $t=0$).
## 2. La Formula Universale (L'approccio Asintotico)
Per qualsiasi circuito del primo ordine (RC o RL), non hai bisogno di risolvere l'equazione differenziale da zero ogni volta. La variabile che cerchi, che chiameremo $x(t)$ (che sarà la tensione $v_C$ per il condensatore o la corrente $i_L$ per l'induttore), segue sempre questa formula magica:

$$x(t) = x(\infty) + [x(0^+) - x(\infty)] \cdot e^{-\frac{t}{\tau}}$$
Dove:
- $x(0^+)$ è il valore iniziale.
- $x(\infty)$ è il valore finale (a regime).
- $\tau$ (tau) è la costante di tempo del circuito.

Il tuo unico compito per risolvere il problema è trovare questi tre numeretti!
## 3. I 4 Passi per la Soluzione
### Passo 1: Analisi per $t < 0$ (Condizione iniziale)
Guarda il circuito prima che l'interruttore scatti. Si suppone che sia acceso da molto tempo e quindi sia "a regime".
Sostituisci il condensatore con un circuito aperto (filo tagliato).
Sostituisci l'induttore con un cortocircuito (filo continuo).
Calcola rispettivamente la tensione $v_C(0^-)$ o la corrente $i_L(0^-)$ con le normali leggi di Ohm e Kirchhoff.
###Passo 2: L'istante $t = 0^+$ (Il principio di continuità)
L'interruttore scatta. Applica la regola d'oro:
$v_C(0^+) = v_C(0^-)$
$i_L(0^+) = i_L(0^-)$
Hai appena trovato il primo pezzo della formula!
### Passo 3: Analisi per $t \to \infty$ (Condizione finale)
Guarda il nuovo circuito (con l'interruttore nella nuova posizione). Immagina che sia passato tantissimo tempo e il transitorio sia finito.
Tratta di nuovo i condensatori come circuiti aperti e gli induttori come cortocircuiti.
Calcola i nuovi valori $v_C(\infty)$ o $i_L(\infty)$.
Hai trovato il secondo pezzo della formula.
### Passo 4: Calcolo della costante di tempo ($\tau$)
Guarda sempre il nuovo circuito (per $t > 0$) e "spegni" tutti i generatori indipendenti (i generatori di tensione diventano cortocircuiti, quelli di corrente circuiti aperti).
Calcola la resistenza equivalente ($R_{eq}$) vista dai "morsetti" del condensatore o dell'induttore.
- Per i circuiti RC: $\tau = R_{eq} \cdot C$
- Per i circuiti RL: $\tau = \frac{L}{R_{eq}}$
## 4. Un Esempio Pratico (Circuito RC)
Immagina un circuito con un generatore di tensione continua $V_s = 10\text{ V}$, in serie a una resistenza $R = 2\text{ k}\Omega$ e un condensatore $C = 5\text{ }\mu\text{F}$.

C'è un interruttore che collega il generatore al circuito e si chiude al tempo $t = 0$. 

Il condensatore è inizialmente scarico. 

Vogliamo trovare l'equazione della tensione sul condensatore $v_C(t)$ per $t > 0$.
Risolviamo con i 4 passi:
- $t < 0$: Il condensatore è scarico, quindi $v_C(0^-) = 0\text{ V}$.
- $t = 0^+$: Per il principio di continuità, $v_C(0^+) = 0\text{ V}$.
- $t \to \infty$: L'interruttore è chiuso da un'eternità. Il condensatore si comporta come un circuito aperto. Non passa corrente nella resistenza, quindi non c'è caduta di tensione su $R$. Tutta la tensione del generatore cade sul condensatore. Quindi: $v_C(\infty) = 10\text{ V}$.
- Calcolo di $\tau$: Spegniamo il generatore di tensione (diventa un corto circuito). La resistenza vista dal condensatore è proprio l'unica resistenza presente.
$R_{eq} = 2\text{ k}\Omega = 2000\text{ }\Omega$
$\tau = R \cdot C = 2000 \cdot (5 \cdot 10^{-6}) = 0.01\text{ s}$

Uniamo i pezzi nella formula:

$$v_C(t) = v_C(\infty) + [v_C(0^+) - v_C(\infty)] \cdot e^{-\frac{t}{\tau}}$$

$$v_C(t) = 10 + [0 - 10] \cdot e^{-\frac{t}{0.01}}$$

$$v_C(t) = 10 \cdot (1 - e^{-100t})\text{ V}$$
Ecco fatto! Hai trovato l'equazione completa. 
Da questa puoi trovare tutto il resto (ad esempio, se vuoi la corrente nel circuito, ti basta fare $i(t) = C \cdot \frac{dv_C}{dt}$).
E i circuiti del Secondo Ordine (RLC)?
