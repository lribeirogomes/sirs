# sirs

Iniciar Sessao:

A:--------{Ks,T,SeqA,SigA(Ks,T,SeqA)}PubB-------->B
A:<-------{SegA+1, SeqB SigB(SegA+1, SeqB)}Ks-----B

Envio SMSs:

A:-{SeqA+1,M1,SigA(M1)}Ks->B
B:-{SeqB+1,M2,SigB(M2)}Ks->A

...
>As chaves assimétricas são geradas (usando RSA ou EC) pela aplicação
>A e B têm as chaves publicas um do outro, e guardam as respectivas chaves privadas
>Quando A quer comunicar com B:
>>A gera uma chave simétrica de sessão nova (Ks)
>>A gera um número de sequência aleatório (SeqA)
>>A gera um KEK contendo: Ks, SeqA, um timestamp (T), e uma assinatura desta informação
>>Este KEK é cifrado com a chave pública de B
>A envia 
