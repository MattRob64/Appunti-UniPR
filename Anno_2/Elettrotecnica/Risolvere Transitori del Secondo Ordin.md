# Risolvere Transitori del Secondo Ordine in Elettrotecnica

1.  **Si determinano dapprima le condizioni iniziali** <br>
    $x(0)$ e $dx(0)/dt$ e il valore finale $x(\infty)$, come in quelli del primo ordine.

2.  **Si determina la risposta transitoria** <br>
    $x_t(t)$ spegnendo i generatori indipendenti e applicando la KCL e la KVL. <br>
    Una volta ottenuta una equazione differenziale del secondo ordine, se ne determinano le radici caratteristiche. <br>
    A seconda che la risposta sia sovrasmorzata, a smorzamento critico o sottosmorzata, si ottiene $x_{SOA}(t)$ con due costanti incognite $A$ e $B$.

3.  **Si ottiene la risposta di regime** come:
    $$x_{IP}(t) = x(\infty)$$
    dove $x(\infty)$ è il valore finale di $x$, ottenuto al passo 1.

4.  **La risposta completa** <br>
    consiste ora nella somma della risposta transitoria e di quella di regime:
    $$x(t) = x_{SOA}(t) + x_{IP}(t) $$
    A seconda delle soluzioni si piò avere una funzione del seguente tipo: <br>
    1. **Soluzioni reali e distinte (Sovrasmorzamento)** 
        $$ x(t) = A \cdot e^{\alpha_1 \cdot t} + B \cdot e^{\alpha_2 \cdot t}+x(\infty)$$
    2. **Soluzioni reali e coincidenti (Smorzamento critico)**
        $$\alpha = \alpha_1 = \alpha_2$$
        $$ x(t) = (A + B) \cdot e^{\alpha \cdot t}+x(\infty)$$
    3. **Soluzioni complesse e coniugate (Sottosmorzamento)**
        $$\alpha_1 = K + jQ$$
        $$\alpha_2 = K - jQ$$
        $$x(t) = e^{K \cdot t} \cdot A \cdot \sin (Q \cdot t + B) + x(\infty)$$
5. **Si determinano infine le costanti associate** <br> 
    alla risposta transitoria, imponendo le condizioni iniziali $x(0)$ e $dx(0)/dt$, determinate nel passo 1:
    $$\begin{cases} 
        x(0^+)=A+B+x(\infty) \\
        \frac{d \; x(0^+)}{dt} = \alpha_1 A + \alpha_2 B
    \end{cases}$$
    Nel caso delle complesse e coniugate:
    $$\begin{cases} 
        x(0^+)=A \cdot \sin(B) + x(\infty) \\
        \frac{d \; x(0^+)}{dt} = \frac{d \; A \cdot \sin(B)}{dt}
    \end{cases}$$
6. **$\tau$ viene invece calcolato come:**
    $$\begin{cases} 
        \tau_1 = -\frac{1}{\alpha_1} \\
        \tau_2 = -\frac{1}{\alpha_2}
    \end{cases}$$
    Nel caso delle complesse e coniugate:
    $$ \tau = \frac{1}{Re(\alpha_1)}$$