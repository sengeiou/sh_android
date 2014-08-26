#import "_Subscription.h"

@interface Subscription : _Subscription {}

typedef enum subscriptionNum : NSUInteger
{
    goles = 4,
    inicio = 2,
    fin = 16,
    suspendido = 32768,
    endmin90 = 128,
    inicioProrroga = 4096,
    finalProrroga = 256,
    inicioPenalties = 8192,
    expulsiones = 8,
    recordatoriounahoraantes = 16384,
    descanso = 32,
    finDescanso = 64,
    alineacion = 131072,
    penaltis = 512,
    penaltiFallado = 2048,
    penaltiRoja = 1024,
    penaltiAmarilla = 1048576,
    amarilla = 65536,
    cambios = 262144,
    ofertadelpartido = 524288,
    cuotas = 2097152
} kSubscriptionNum;


+(Subscription *)insertWithDictionary:(NSDictionary *)dict;
+(Subscription *)updateWithDictionary:(NSDictionary *)dict;
+(Subscription *)createTemporarySubscription;
+(Subscription *)createTemporarySubscriptionWithSubscription:(Subscription *)subscription;

-(NSUInteger)getSubscriptionsNumber;

-(BOOL)isInicioBlockOn:(Subscription *)subscription;
-(BOOL)isAdicionalBlockOn:(Subscription *)subscription;
-(BOOL)isOnSubscription:(kSubscriptionNum)subscription;

@end
