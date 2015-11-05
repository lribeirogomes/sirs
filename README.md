# sirs

Iniciar Sessao:

A:--------{Ks,T,SeqA,SigA(Ks,T,SeqA)}PubB-------->B

A:<-------{SeqB, SigB(SeqB)}Ks-----B

Envio SMSs:

A:-{SeqA+1,M1,SigA(M1)}Ks->B

B:-{SeqB+1,M2,SigB(M2)}Ks->A

...

>As chaves assimétricas são geradas pela aplicação, usando um keygen de RSA ou EC
>A e B guardam as respectivas chaves privadas
>Existe um ficheiro que mapeia o número de telefone às chaves públicas correspondentes
>A e B exportam as chaves públicas para esse ficheiro
>A e B partilham esse ficheiro para obter as chaves publicas um do outro

>Quando A quer comunicar com B:

>>A gera uma chave simétrica de sessão nova (Ks) com AES

>>A gera um número de sequência aleatório (SeqA)
>>Este número vai servir para B ordenar as mensagens e evitar replay-attacks dentro da sessão

>>A gera um KEK contendo: Ks, SeqA, um timestamp (T), e uma assinatura desta informação

>>Este KEK é cifrado com a chave pública de B, e enviado a B

>>Confirmar início de sessão:
>>B verifica se o timestamp é válido, e se já existe uma sessão activa com o mesmo timastamp, do mesmo remetente
//B responde com um ChallengeResponse (incrementa SeqA), envia SeqB (também aleatŕio), e assina esta informação com a sua chave privada
>>A resposta de B é encriptada com Ks recebido de A

>>Envio SMS:
>>Verifica se a sessão expirou
>>Se sim, inicia-se uma nova sessão, em caso contrário envia:
>>>Incremento do numSeq do remetente
>>>Mensagem
>>>Assinatura da mensagem
>>Tudo encriptado com Ks

>>Recepção SMS:
>>Verifica se a sessão expirou
>>Se sim, ignora a mensagem, em caso conntrário, verifica a assinatura
>>Ordena mensagens de acordo com o remetente e o numSeq

