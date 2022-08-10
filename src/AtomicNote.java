import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicNote {


    public static final int BEER_PACK = 12;
    public static int beersOnFreezer;
    public static AtomicBoolean note;

    public static int tooManyBeers = 0;

    public static final int TRIES = 100_000;

    private static final Runnable student = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < TRIES; i ++) {
                if(beersOnFreezer > 0) {
                    //System.out.println(beersOnFreezer + " beers on freezes, getting one, will be " + (beersOnFreezer - 1));
                    beersOnFreezer = beersOnFreezer - 1;
                }

                if(beersOnFreezer == 0) {
                    //System.out.println("0 beers on freezer, going to buy");

                    if(note.compareAndSet(false, true)) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        beersOnFreezer += BEER_PACK;
                        note.set(false);

                        if(beersOnFreezer > BEER_PACK) {
                            System.out.println("too many beers " + beersOnFreezer);
                            tooManyBeers ++;
                        }

                    }

                    //System.out.println("now with "+beersOnFreezer+" beers");
                }
            }
        }
    };

    public static void main(String[] args) throws InterruptedException {
        beersOnFreezer = BEER_PACK;
        note = new AtomicBoolean(false);

        var student1 = new Thread(student);
        var student2 = new Thread(student);

        student1.start();
        student2.start();

        student1.join();
        student2.join();

        System.out.println(tooManyBeers + " TIMES WITH TOO MANY BEERS");
    }


}
