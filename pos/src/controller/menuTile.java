package controller;

import io.MenuHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sale.item.Item;
import util.RuntimeHelper;

import java.io.IOException;

public final class menuTile extends FXMLController {

    /**
     * point of sale base fxml document location.
     */
    public static final String MENU_TILE_FXML = "../fxml/menuItemTile.fxml";

    /**
     * What menu item this tile represents
     */
    private Item item = Item.NULL_ITEM;

    //#region FXML references
    @FXML
    private Label lblPrice;

    @FXML
    private Label lblName;

    @FXML
    private ImageView itemImage;

    //#endregion

    /**
     * Creates a new FXML controller.
     */
    public menuTile() {
        super(MENU_TILE_FXML);
    }
    /**
     * Sets the item this tile represents, and the parent it belongs to; then updates the UI elements accordingly.
     * @param _item item this tile represents
     */
    public void setItem(Item _item){
        item = _item;
        lblPrice.setText(item.price().asDisplay());
        lblName.setText(item.name());
        setImage();
    }

    /**
     * Menu tile has been tapped on the POS window. Adds the item to the active order.
     */
    public void select(){
            ((pos)parentController)                                                                                     // Get the controller of the window (parent to this menu item tile) and cast it to a pos controller to get access to the jfx injected objects.
                    .addOrderItem(item);                                                                                // Add the item to the active order.
    }

    private void setImage() {
        Image image = MenuHelper.DEFAULT_ITEM_IMG;
        try {
            image = new Image(MenuHelper.class.getResource("/img/" + item.name() + ".png").toString());
        } catch (Exception e){
            RuntimeHelper.log(this, "[WARN] " + item.name() + " has no matching image!");
        }
        finally {
            itemImage.setImage(image);
        }
    }

    public static FXMLController create(FXMLController controller, Item _item) throws IOException {
        menuTile c = (menuTile) FXMLController.create(MENU_TILE_FXML, controller);
        c.setItem(_item);
        return c;
    }

}
