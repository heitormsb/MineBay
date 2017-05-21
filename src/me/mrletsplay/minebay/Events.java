package me.mrletsplay.minebay;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.EconomyResponse;

public class Events implements Listener{
	
	public static HashMap<Player,Integer> changeName = new HashMap<>();
	

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		if(e.getInventory().getName().equals(Config.simpleReplace(Config.Config.getString("minebay.prefix")))){
			try{
				if(e.getInventory().getSize() == 9*6){
					String mode = e.getInventory().getItem(45).getItemMeta().getDisplayName();
					if(mode.equals("�8Auction Room")){
						int page = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(0)));
						int roomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(1)));
						AuctionRoom r = AuctionRooms.getAuctionRoomByID(roomID);
						if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta()){
							if(e.getCurrentItem().getItemMeta().hasDisplayName()){
								String name = e.getCurrentItem().getItemMeta().getDisplayName();
								if(name.equals("�0")){
									e.setCancelled(true);
									return;
								}else if(name.equals("�7Previous page")){
									Inventory newInv = r.getMineBayInv(page-1, (Player)e.getWhoClicked());
									if(newInv!=null){
										MineBay.changeInv(e.getInventory(), newInv);
									}
									e.setCancelled(true);
									return;
								}else if(name.equals("�7Next page")){
									Inventory newInv = r.getMineBayInv(page+1, (Player)e.getWhoClicked());
									if(newInv!=null){
										MineBay.changeInv(e.getInventory(), newInv);
									}
									e.setCancelled(true);
									return;
								}
							}
							if(e.getCurrentItem().getItemMeta().hasLore() && (e.getCurrentItem().getItemMeta().getLore().size() == 3 || e.getCurrentItem().getItemMeta().getLore().size() == 4)){
								int id = Integer.parseInt(Config.onlyDigitsNoColor(e.getCurrentItem().getItemMeta().getLore().get(2)));
								SellItem it = r.getItemByID(id);
								if(it.getSeller()!=null && !it.getSeller().equals(e.getWhoClicked().getName())){
									e.getWhoClicked().closeInventory();
									MineBay.showPurchaseConfirmDialog((Player)e.getWhoClicked(), it);
								}else{
									HashMap<Integer,ItemStack> excess = e.getWhoClicked().getInventory().addItem(it.getItem());
									for(Map.Entry<Integer, ItemStack> me : excess.entrySet()){
										e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), me.getValue());
									}
									r.removeSellItem(id);
									e.getWhoClicked().closeInventory();
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.retract-sale.success")));
								}
							}
						}
					}else if(mode.equals("�8Auction Rooms")){
						if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
							String name = e.getCurrentItem().getItemMeta().getDisplayName();
							int page = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(0)));
							String search = e.getInventory().getItem(45).getItemMeta().getLore().get(1).replace("�8Owner: �7", "");
							if(name.equals("�7Previous page")){
								Inventory newInv = MineBay.getRoomSelectionMenu(page-1, search, (Player)e.getWhoClicked());
								if(newInv!=null){
									MineBay.changeInv(e.getInventory(), newInv);
								}
								e.setCancelled(true);
								return;
							}else if(name.equals("�7Next page")){
								Inventory newInv = MineBay.getRoomSelectionMenu(page+1, search, (Player)e.getWhoClicked());
								if(newInv!=null){
									MineBay.changeInv(e.getInventory(), newInv);
								}
								e.setCancelled(true);
								return;
							}else if(name.equals("�7Your Rooms")){
								MineBay.changeInv(e.getInventory(), MineBay.getRoomSelectionMenu(0, e.getWhoClicked().getName(), (Player)e.getWhoClicked()));
							}else if(name.equals("�7All Rooms")){
								MineBay.changeInv(e.getInventory(), MineBay.getRoomSelectionMenu(0, "all", (Player)e.getWhoClicked()));
							}else if(e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().size() >= 4){
								if(e.getClick().equals(ClickType.LEFT)){
									int clRoomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getCurrentItem().getItemMeta().getLore().get(3)));
									AuctionRoom r = AuctionRooms.getAuctionRoomByID(clRoomID);
									MineBay.changeInv(e.getInventory(), r.getMineBayInv(0, (Player)e.getWhoClicked()));
								}else if(e.getClick().equals(ClickType.RIGHT) && e.getCurrentItem().getItemMeta().getLore().size() == 5){
									int clRoomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getCurrentItem().getItemMeta().getLore().get(3)));
									AuctionRoom r = AuctionRooms.getAuctionRoomByID(clRoomID);
									MineBay.changeInv(e.getInventory(), r.getSettingsMenu());
								}
							}
						}
					}else if(mode.equals("�8Settings")){
						if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
							String name = e.getCurrentItem().getItemMeta().getDisplayName();
							int roomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(0)));
							AuctionRoom r = AuctionRooms.getAuctionRoomByID(roomID);
							if(name.equals("�7Change Name")){
								Events.changeName.put((Player)e.getWhoClicked(), roomID);
								e.getWhoClicked().closeInventory();
								e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.newname")));
							}else if(name.equals("�7Change Block")){
								e.getWhoClicked().closeInventory();
								e.getWhoClicked().openInventory(r.getBlockSelectionInv());
								r.saveAllSettings();
								r.updateSettings();
								MineBay.updateRoomSelection();
							}else if(name.equals("�7Buy 1 slot")){
								if(r.getSlots() < Config.Config.getInt("minebay.user-rooms.max-slots")){
									EconomyResponse re = Main.econ.withdrawPlayer((OfflinePlayer)e.getWhoClicked(), Config.Config.getInt("minebay.user-rooms.slot-price"));
									if(re.transactionSuccess()){
										r.setSlots(r.getSlots()+1);
										r.saveAllSettings();
										r.updateSettings();
										MineBay.updateRoomSelection();
										e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.slot-buy.success")));
									}
								}else{
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.slot-buy.toomanyslots")));
								}
							}else if(name.equals("�7Sell 1 slot")){
								if(r.getSlots() > Config.Config.getInt("minebay.user-rooms.default-slot-number")){
									EconomyResponse re = Main.econ.depositPlayer((OfflinePlayer)e.getWhoClicked(), Config.Config.getInt("minebay.user-rooms.slot-price"));
									if(re.transactionSuccess()){
										r.setSlots(r.getSlots()-1);
										r.saveAllSettings();
										r.updateSettings();
										MineBay.updateRoomSelection();
										e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.slot-sell.success")));
									}
								}else{
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.slot-sell.notenoughslots")));
								}
							}else if(name.equals("�7Increase Tax")){
								if(r.getTaxshare()<Config.Config.getInt("minebay.user-rooms.max-tax-percent")){
									r.setTaxshare(r.getTaxshare()+1);
									r.saveAllSettings();
									r.updateSettings();
									MineBay.updateRoomSelection();
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.tax.success")).replace("%newtax%", ""+r.getTaxshare()));
								}else{
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.tax.toohigh")));
								}
							}else if(name.equals("�7Decrease Tax")){
								if(r.getTaxshare()>0){
									r.setTaxshare(r.getTaxshare()-1);
									r.saveAllSettings();
									r.updateSettings();
									MineBay.updateRoomSelection();
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.tax.success")).replace("%newtax%", ""+r.getTaxshare()));
								}else{
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.tax.toolow")));
								}
							}else if(name.equals("�cDelete Room")){
								if(r.getSoldItems().isEmpty()){
									int sl = (r.getSlots() - Config.Config.getInt("minebay.user-rooms.default-slot-number"))*Config.Config.getInt("minebay.user-rooms.slot-price");
									int pr = Config.Config.getInt("minebay.user-rooms.room-price");
									EconomyResponse re = Main.econ.depositPlayer((Player)e.getWhoClicked(), sl+pr);
									if(re.transactionSuccess()){
										AuctionRooms.deleteAuctionRoom(roomID);
										for(Player p : Bukkit.getOnlinePlayers()){
											String t = MineBay.getInvType(p);
											if(t.equals("auction room")){
												int plRoomID = Integer.parseInt(Config.onlyDigitsNoColor(p.getOpenInventory().getTopInventory().getItem(45).getItemMeta().getLore().get(1)));
												if(plRoomID == roomID){
													p.closeInventory();
												}
											}
										}
										MineBay.updateRoomSelection();
										e.getWhoClicked().closeInventory();
										e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell-room.success")).replace("%price%", ""+(sl+pr)));
									}else{
										e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell-room.error")).replace("%error%", re.errorMessage));
									}
								}else{
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell-room.not-empty")));
								}
							}
						}
					}else if(mode.equals("�8Sell Item")){
						if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
							String name = e.getCurrentItem().getItemMeta().getDisplayName();
							int page = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(0)));
							String search = e.getInventory().getItem(45).getItemMeta().getLore().get(1).replace("�8Owner: �7", "");
							if(name.equals("�7Previous page")){
								int price = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(2)));
								Inventory newInv = MineBay.getSellRoomSelectionMenu(page-1, search, price);
								if(newInv!=null){
									MineBay.changeInv(e.getInventory(), newInv);
								}
								e.setCancelled(true);
								return;
							}else if(name.equals("�7Next page")){
								int price = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(2)));
								Inventory newInv = MineBay.getSellRoomSelectionMenu(page+1, search, price);
								if(newInv!=null){
									MineBay.changeInv(e.getInventory(), newInv);
								}
								e.setCancelled(true);
								return;
							}else if(name.equals("�7Your Rooms")){
								int price = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(2)));
								MineBay.changeInv(e.getInventory(), MineBay.getSellRoomSelectionMenu(0, e.getWhoClicked().getName(), price));
							}else if(name.equals("�7All Rooms")){
								int price = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(2)));
								MineBay.changeInv(e.getInventory(), MineBay.getSellRoomSelectionMenu(0, "all", price));
							}else if(e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().size() >= 4){
								int roomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getCurrentItem().getItemMeta().getLore().get(3)));
								AuctionRoom r = AuctionRooms.getAuctionRoomByID(roomID);
								if(r.getOccupiedSlots() < r.getSlots() || r.getSlots() == -1){
									if(r.getSoldItemsBySeller(e.getWhoClicked().getName()).size() < Config.Config.getInt("minebay.user-rooms.offers-per-slot")){
										if(e.getWhoClicked().getItemInHand()!=null && !e.getWhoClicked().getItemInHand().getType().equals(Material.AIR)){
											SellItem it = new SellItem(((Player)e.getWhoClicked()).getItemInHand(), r, e.getWhoClicked().getName(), Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(45).getItemMeta().getLore().get(2))), r.getNewItemID());
											r.addSellItem(it);
											((Player)e.getWhoClicked()).setItemInHand(new ItemStack(Material.AIR));
											e.getWhoClicked().closeInventory();
											e.getWhoClicked().sendMessage(Config.replaceForSellItem(Config.simpleReplace(Config.Config.getString("minebay.info.sell.success")), it));
										}else{
											e.getWhoClicked().closeInventory();
											e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell.error.noitem")));
										}
									}else{
										e.getWhoClicked().closeInventory();
										e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell.error.too-many-sold")));
									}
								}else{
									e.getWhoClicked().closeInventory();
									e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.sell.error.no-slots")));
								}
							}
						}
					}
				}else if(e.getInventory().getSize() == 3*9){
					String mode = e.getInventory().getItem(18).getItemMeta().getDisplayName();
					int roomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(18).getItemMeta().getLore().get(0)));
					AuctionRoom r = AuctionRooms.getAuctionRoomByID(roomID);
					if(mode.equals("�8Change Block")){
						if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
							String name = e.getCurrentItem().getItemMeta().getDisplayName();
							if(name.contains("�7Block | ")){
								r.setIconMaterial(e.getCurrentItem().getType());
								e.getWhoClicked().closeInventory();
								r.saveAllSettings();
								r.updateSettings();
								r.updateMineBay();
								MineBay.updateRoomSelection();
								e.getWhoClicked().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.newblock-applied")).replace("%type%", e.getCurrentItem().getType().name().toLowerCase().replace("_", " ")));
							}
						}
					}
				}
				e.setCancelled(true);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if(e.getInventory().getName().equals(Config.simpleReplace(Config.Config.getString("minebay.prefix"))+" �8Confirm")){
			try{
				if(e.getCurrentItem()!=null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName() && e.getInventory().getItem(0).getItemMeta().hasLore() && e.getInventory().getItem(0).getItemMeta().getLore().size() == 4){
					String name = e.getCurrentItem().getItemMeta().getDisplayName();
					if(name.equals("�aConfirm")){
						int id = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(0).getItemMeta().getLore().get(2)));
						int roomID = Integer.parseInt(Config.onlyDigitsNoColor(e.getInventory().getItem(0).getItemMeta().getLore().get(3)));
						AuctionRoom r = AuctionRooms.getAuctionRoomByID(roomID);
						SellItem it = AuctionRooms.getAuctionRoomByID(roomID).getItemByID(id);
						EconomyResponse re = Main.econ.withdrawPlayer((OfflinePlayer)e.getWhoClicked(), it.getPrice());
						OfflinePlayer seller = Bukkit.getOfflinePlayer(it.getSeller());
						OfflinePlayer owner = null;
						if(r.getOwner()!=null){
							owner = Bukkit.getOfflinePlayer(r.getOwner());
						}
						double sellerAm = round((double)((100-r.getTaxshare())*0.01)*it.getPrice(),5);
						double ownerAm = round((double)(r.getTaxshare()*0.01)*it.getPrice(),5);
						EconomyResponse r2 = Main.econ.depositPlayer(seller, sellerAm);
						EconomyResponse r3 = null;
						if(owner!=null){
							r3 = Main.econ.depositPlayer(owner, ownerAm);
						}
						if(re.transactionSuccess() && r2.transactionSuccess()){
							if((owner!=null && r3!=null && r3.transactionSuccess()) || owner==null){
								e.getWhoClicked().sendMessage(Config.replaceForSellItem(Config.simpleReplace(Config.Config.getString("minebay.purchase.success")), it));
								r.removeSellItem(id);
								r.updateMineBay();
								Player p = (Player)e.getWhoClicked();
								HashMap<Integer,ItemStack> excess = p.getInventory().addItem(it.getItem());
								for(Map.Entry<Integer, ItemStack> me : excess.entrySet()){
									p.getWorld().dropItem(p.getLocation(), me.getValue());
								}
								e.getWhoClicked().closeInventory();
								if(seller.isOnline()){
									((Player)seller).sendMessage(Config.replaceForSellItem(Config.simpleReplace(Config.Config.getString("minebay.info.sell.seller.success")), it).replace("%buyer%", e.getWhoClicked().getName()).replace("%price2%", ""+sellerAm));
								}
								if(owner!=null && owner.isOnline() && r.getTaxshare() > 0){
									((Player)owner).sendMessage(Config.replaceForSellItem(Config.simpleReplace(Config.Config.getString("minebay.info.sell.owner.success")), it).replace("%buyer%", e.getWhoClicked().getName()).replace("%price2%", ""+ownerAm));
								}
							}
						}else{
							e.getWhoClicked().sendMessage(Config.replaceForSellItem(Config.simpleReplace(Config.Config.getString("minebay.purchase.error")), it).replace("%error%", re.errorMessage));
							e.getWhoClicked().closeInventory();
						}
					}else if(name.equals("�cCancel")){
						e.getWhoClicked().closeInventory();
					}
				}
				e.setCancelled(true);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(changeName.keySet().contains(e.getPlayer())){
			String nName = e.getMessage();
			int room = changeName.get(e.getPlayer());
			if(nName.length()<=Config.Config.getInt("minebay.user-rooms.max-name-length")){
				changeName.remove(e.getPlayer());
				AuctionRoom r = AuctionRooms.getAuctionRoomByID(room);
				r.setName(nName);
				r.saveAllSettings();
				r.updateSettings();
				MineBay.updateRoomSelection();
				e.getPlayer().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.newname-applied")).replace("%newname%", nName));
			}else{
				e.getPlayer().sendMessage(Config.simpleReplace(Config.Config.getString("minebay.info.error.name-too-long")));
			}
			e.setCancelled(true);
		}
	}
	
	private static double round (double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
	
}