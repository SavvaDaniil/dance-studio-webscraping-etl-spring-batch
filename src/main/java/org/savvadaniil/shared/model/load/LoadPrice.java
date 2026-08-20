package org.savvadaniil.shared.model.load;

public class LoadPrice extends org.savvadaniil.shared.model.stage.Price {

    private int id;

    public LoadPrice(int id, String title, int price) {
        super(title, price);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
