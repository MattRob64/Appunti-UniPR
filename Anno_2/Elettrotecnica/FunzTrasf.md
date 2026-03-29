# Il Contesto: Cos'è il Regime Sinusoidale?
Prima di tutto, "regime sinusoidale" significa che stiamo alimentando il nostro circuito non con una batteria a corrente continua (come una pila), ma con un generatore a corrente alternata (AC), il cui segnale oscilla nel tempo come un'onda (una sinusoide). <br>
Ogni segnale sinusoidale ha due caratteristiche fondamentali: <br>
- Ampiezza: Quanto è "alta" l'onda (es. 10 Volt).
- Fase: Quando l'onda inizia rispetto a un punto di riferimento (un ritardo o un anticipo nel tempo).
- Frequenza ($f$ o $\omega$): Quante oscillazioni fa in un secondo. 

Per i calcoli in elettrotecnica usiamo la pulsazione $\omega = 2\pi f$. <br>
Per semplificare i calcoli, gli ingegneri usano i numeri complessi (fasori) per rappresentare l'ampiezza e la fase in un colpo solo, indicando la frequenza con la variabile $j\omega$ (dove $j$ è l'unità immaginaria).

# La Funzione di Trasferimento: $H(j\omega)$
Immagina il tuo circuito come una "scatola nera". <br>
Fai entrare un segnale (Input) ed esce un altro segnale (Output).<br>
La Funzione di Trasferimento, che chiamiamo comunemente $H(j\omega)$, è semplicemente il rapporto tra il segnale di uscita e il segnale di ingresso.

$$H(j\omega) = \frac{\text{Output}(j\omega)}{\text{Input}(j\omega)}$$

Questa funzione ci dice come la scatola nera modifica il segnale in base alla frequenza che stiamo usando. <br> 
Poiché $H(j\omega)$ è un numero complesso, ci dà due informazioni vitali:
- Il Modulo $|H(j\omega)|$: Ci dice di quanto il segnale viene amplificato o attenuato. <br>
Se il modulo è $2$, il segnale in uscita è il doppio dell'ingresso. Se è $0.5$, si è dimezzato.
- La Fase $\angle H(j\omega)$: Ci dice di quanto il segnale viene sfasato (ritardato o anticipato) in uscita rispetto all'ingresso.
# I Diagrammi di Bode: Disegnare la Funzione
Calcolare il modulo e la fase per ogni singola frequenza possibile a mano sarebbe un incubo. <br>
Hendrik Bode ha inventato un modo grafico per visualizzare il comportamento del circuito per tutte le frequenze su una scala vastissima.<br>
I diagrammi di Bode sono sempre composti da due grafici separati, entrambi disegnati con l'asse orizzontale (le frequenze) in scala logaritmica (questo ci permette di vedere contemporaneamente cosa succede a $10$ Hz, $1.000$ Hz e $1.000.000$ Hz senza aver bisogno di un foglio lungo chilometri).
### Grafico 1: Il Modulo (in Decibel)
L'asse verticale non mostra un semplice moltiplicatore (es. $\times 2$ o $\times 10$), ma utilizza i Decibel (dB). <br>
La formula per convertire il modulo in dB è:

$$|H(j\omega)|_{dB} = 20 \log_{10}(|H(j\omega)|)$$

Se i dB sono positivi, il circuito amplifica.<br>
Se i dB sono a $0$, il segnale esce identico a come è entrato (modulo $= 1$).<br>
Se i dB sono negativi, il circuito attenua (filtra) il segnale.
### Grafico 2: La Fase
L'asse verticale mostra semplicemente l'angolo di sfasamento in gradi (°) o radianti. <br>
Spesso varia tra $+90^\circ$ e $-90^\circ$ (o multipli) a seconda di quanti condensatori e induttori ci sono nel circuito.
# Un Esempio Pratico: Il Filtro Passa-Basso RC
Vediamo come si applica tutto questo al circuito più famoso: il filtro passa-basso formato da una Resistenza ($R$) e un Condensatore ($C$) in serie.

Prendiamo il segnale di ingresso ai capi di tutta la serie ($R+C$) e leggiamo l'uscita solo ai capi del condensatore ($C$).<br>
Applicando la regola del partitore di tensione, la funzione di trasferimento risulta:

$$H(j\omega) = \frac{1}{1 + j\omega RC}$$

Senza fare troppi calcoli, pensiamo logicamente a cosa fa questo circuito alle frequenze estreme:
- A frequenze bassissime ($\omega \to 0$): Il condensatore si comporta come un circuito aperto. <br>
La corrente non passa, quindi non c'è caduta di tensione sulla resistenza. <br>
L'uscita è uguale all'ingresso.
    - Modulo: $1$ (che corrisponde a $0$ dB).
    - Fase: $0^\circ$.
- A frequenze altissime ($\omega \to \infty$): Il condensatore si comporta come un cortocircuito. La tensione ai suoi capi crolla a zero.
    - Modulo: $0$ (che corrisponde a $-\infty$ dB). Il segnale viene bloccato!
    - Fase: Tende a $-90^\circ$.

C'è un punto cruciale nel mezzo, chiamato Frequenza di Taglio ($\omega_c = \frac{1}{RC}$). <br>
È il punto in cui il grafico "curva" bruscamente verso il basso. A questa frequenza, la potenza del segnale si dimezza (il che si traduce in una caduta esatta di $-3$ dB sul grafico) e la fase è esattamente di $-45^\circ$.<br>
Come puoi vedere dal grafico (se lo immaginiamo o guardiamo l'immagine inserita), il diagramma di Bode del modulo rimane piatto a $0$ dB per le basse frequenze, per poi "scendere a scivolo" dopo la frequenza di taglio. <br>
Ecco perché si chiama "passa-basso": lascia passare le basse frequenze e uccide quelle alte.

In sintesi: La funzione di trasferimento ti dice matematicamente come il circuito manipola le onde. <br>
I diagrammi di Bode ti fanno vedere questa manipolazione in un colpo d'occhio per qualsiasi frequenza.