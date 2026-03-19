package com.mygdx.LordOfTheDices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CombatScreen implements Screen {

    private Stage stage;

    // Dokular (Kendi dosya yollarınla değiştirmeyi unutma)
    private Texture cardSpade, cardHeart, cardClub, cardDiamond;
    private Texture lockIcon, emptySlotTexture, leftArrowTexture, rightArrowTexture, diceIcon, background;

    // Arayüz Tabloları
    private Table rootTable;
    private Table rightPanelTable;
    private Table bottomAreaWrapper; // Görünümleri değiştireceğimiz ana taşıyıcı
    private Table firstViewTable;    // 4 Kartın olduğu ilk görünüm
    private Table secondViewTable;   // Seçili kart ve yuvaların olduğu ikinci görünüm

    public CombatScreen() {
        // 800x480 çözünürlüğünde piksel-perfect bir ekran
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        loadTextures();
        buildUI();
    }

    private void loadTextures() {
        // Örnek yüklemeler (Kendi assets klasörüne göre düzenle)
        
        cardSpade = new Texture("Cards\\card-spades.png");
        cardClub = new Texture("Cards\\card-clubs.png");
        cardHeart = new Texture("Cards\\card-hearts.png");
        cardDiamond = new Texture("Cards\\card-diamonds.png");
        background = new Texture("FightBackground.png");
        diceIcon = TextureRegion.split(new Texture("dice.png"), 32, 32)[0][0].getTexture();
    }

    private void buildUI() {
        // 1. KÖK TABLO (Tüm ekranı kaplar)
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Image Bg = new Image(background);
        Bg.setFillParent(true);
        
        stage.addActor(Bg);
        Bg.toBack();
        // 2. SAĞ PANEL (Kilitler)
        rightPanelTable = new Table();
        for (int i = 0; i < 6; i++) {
            // Şimdilik null olmaması için yeni Image() içini boş bıraktım, sen lockIcon koyacaksın
            rightPanelTable.add(new Image()).padBottom(5).row(); 
        }

        // 3. ALT TAŞIYICI (WRAPPER)
        bottomAreaWrapper = new Table();

        // 4. İLK GÖRÜNÜM (4 Kart)
        firstViewTable = new Table();
        Image spadeImg = new Image(cardSpade);
        
        // Karta Tıklama Olayı (Animasyonu Başlatır)
        spadeImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Tıklanan kartı ve dokusunu animasyon metoduna gönderiyoruz
                playTransitionAnimation(spadeImg, cardSpade);
            }
        });

        firstViewTable.add(spadeImg).pad(10);
        firstViewTable.add(new Image(cardHeart)).pad(10);
        firstViewTable.add(new Image(cardClub)).pad(10);
        firstViewTable.add(new Image(cardDiamond)).pad(10);

        // 5. İKİNCİ GÖRÜNÜM (Oklar ve 5 Yuva)
        secondViewTable = new Table();
        //secondViewTable.add(new Image(leftArrowTexture)).padRight(10);
        secondViewTable.add(new Image(cardSpade)); // Ortadaki büyük seçili kart
        //secondViewTable.add(new Image(rightArrowTexture)).padLeft(10);
        
        secondViewTable.add().width(50); // Araya 50 piksellik boşluk
        
        for (int i = 0; i < 5; i++) {
            secondViewTable.add(new Image(cardSpade)).pad(5);
        }

        // Başlangıçta taşıyıcıya ilk görünümü ekle
        bottomAreaWrapper.add(firstViewTable);

        // 6. EKRANI BİRLEŞTİR (Lego gibi diziyoruz)
        rootTable.add().expand().fill(); // Üst boş alan (Savaş alanı)
        rootTable.add(rightPanelTable).width(60).expandY().fillY(); // Sağ panel
        rootTable.row();
        rootTable.add(bottomAreaWrapper).expandX().bottom().padBottom(20).colspan(2); // Alt kısım
    }

    // --- ANİMASYON VE GEÇİŞ MANTIĞI ---
    private void playTransitionAnimation(Image clickedCard, Texture cardTexture) {
        // 1. Tıklanan kartın ekrandaki gerçek (Stage) koordinatlarını bul
        Vector2 startCoords = clickedCard.localToStageCoordinates(new Vector2(0, 0));

        // 2. Arayüzü anında temizle (Gerçek kartlar kaybolur)
        bottomAreaWrapper.clearChildren();

        // 3. KOPYA KART OLUŞTUR VE SAHNEYE EKLE (Gerçek kartın tam üstüne)
        final Image dummyMainCard = new Image(cardTexture);
        dummyMainCard.setPosition(startCoords.x, startCoords.y);
        stage.addActor(dummyMainCard);

        // İkinci ekrandaki kartın duracağı tahmini hedef koordinatlar
        float targetMainX = 150f; 
        float targetMainY = 20f;  

        // 4. ALTINDAN ÇIKACAK 5 KOPYA ZAR/KART İÇİN ANİMASYON
        float targetSlotBaseX = 350f; // Yuvaların tahmini başlangıç X'i
        
        for (int i = 0; i < 5; i++) {
            final Image dummySlotCard = new Image(cardHeart); // Kendi zarlarını/kartlarını koyabilirsin
            dummySlotCard.setPosition(startCoords.x, startCoords.y); // Ana kartın altından başlar
            dummySlotCard.toBack(); // Ana kartın arkasında kalsın
            stage.addActor(dummySlotCard);

            float targetSlotX = targetSlotBaseX + (i * 60f); // Yan yana dizilmeleri için X'i arttır

            // Küçük kartlara sırayla aksiyon ver (Gecikme + Hareket + En son silinme)
            dummySlotCard.addAction(Actions.sequence(
                Actions.delay(i * 0.1f), // Şşt-şşt-şşt efekti
                Actions.moveTo(targetSlotX, targetMainY, 0.4f, Interpolation.exp10Out),
                Actions.removeActor() // Animasyon bitince kopyayı sahneden sil
            ));
        }

        // 5. ANA KARTIN ANİMASYONU VE GERÇEK ARAYÜZÜN YÜKLENMESİ
        dummyMainCard.addAction(Actions.sequence(
            // Hedefe doğru kay
            Actions.moveTo(targetMainX, targetMainY, 0.5f, Interpolation.swingOut),
            // Biraz bekle (Tüm küçük kartların yerine oturması için)
            Actions.delay(0.2f),
            // Animasyon tamamen bitince çalışacak kod bloğu:
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    dummyMainCard.remove(); // Ana kopyayı sil
                    bottomAreaWrapper.add(secondViewTable); // GERÇEK tıklanabilir arayüzü ekle!
                }
            })
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        // Texture'ları dispose etmeyi unutma:
        // cardSpade.dispose(); vs..
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}