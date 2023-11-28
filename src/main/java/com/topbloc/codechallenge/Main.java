package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;

import static spark.Spark.*;

import org.json.simple.JSONArray;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();
        // Don't change this - required for GET and POST requests with the header
        // 'content-type'
        options("/*",
                (req, res) -> {
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });

        // Don't change - if required you can reset your database by hitting this
        // endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        // TODO: Add your routes here. a couple of examples are below
        get("/items", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getItems();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        get("/version", (req, res) -> {
            String result = null;
            result = Helpers.handleResponse("success", "TopBloc Code Challenge v1.0");
            res.status(200);
            res.body(result);
            return res.body();
        });

        get("/inventory/items", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getInventory();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });
        get("/inventory/out-of-stock", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getOutOfStock();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });
        get("/inventory/overstock", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getOverStock();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });
        get("/inventory/understock", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getUnderStock();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });
        get("/inventory/items/:itemId", (req, res) -> {
            String result = null;
            try {
                String id = req.params(":itemId");
                if (id != null) {
                    JSONArray data = DatabaseManager.getItemById(id);
                    result = Helpers.handleResponse("success", data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        get("/distributors", (req, res) -> {
            String result = null;
            try {
                JSONArray data = DatabaseManager.getDistributors();
                result = Helpers.handleResponse("success", data);
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        get("/distributors/:distributorId/items", (req, res) -> {
            String result = null;
            try {
                String id = req.params(":distributorId");
                if (id != null) {
                    JSONArray data = DatabaseManager.getDistributorItemsByDistributorId(id);
                    result = Helpers.handleResponse("success", data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        get("/distributors/items/:itemId", (req, res) -> {
            String result = null;
            try {
                String id = req.params("itemId");
                if (id != null) {
                    JSONArray data = DatabaseManager.getDistributorItemsByItemId(id);
                    result = Helpers.handleResponse("success", data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        post("/items", (req, res) -> {
            String result = null;
            try {
                String itemData = req.body();
                if (itemData != null) {
                    Integer data = DatabaseManager.createItem(itemData);
                    result = Helpers.handleResponse("success", "Item ID '" + data + "' created.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        post("/inventory", (req, res) -> {
            String result = null;
            try {
                String inventoryData = req.body();
                if (inventoryData != null) {
                    Integer data = DatabaseManager.createInventory(inventoryData);
                    result = Helpers.handleResponse("success", "Inventory ID '" + data + "' created.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        put("/inventory/:inventoryId", (req, res) -> {
            String result = null;
            try {
                String id = req.params(":inventoryId");
                String inventoryData = req.body();
                if (id != null && inventoryData != null) {
                    Integer data = DatabaseManager.modifyInventory(id, inventoryData);
                    result = Helpers.handleResponse("success", "Inventory ID '" + data + "' updated.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        post("/distributors", (req, res) -> {
            String result = null;
            try {
                String distributorData = req.body();
                if (distributorData != null) {
                    Integer data = DatabaseManager.createDistributor(distributorData);
                    result = Helpers.handleResponse("success", "Distributor ID '" + data + "' created.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        post("/distributors/items", (req, res) -> {
            String result = null;
            try {
                String distributorItemData = req.body();
                if (distributorItemData != null) {
                    Integer data = DatabaseManager.createDistributorItem(distributorItemData);
                    result = Helpers.handleResponse("success", "Distributor Item  ID '" + data + "' created.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        put("/distributors/items/:itemId", (req, res) -> {
            String result = null;
            try {
                String id = req.params(":itemId");
                String distributorPriceData = req.body();

                if (id != null && distributorPriceData != null) {
                    Integer data = DatabaseManager.modifyInventory(id, distributorPriceData);
                    result = Helpers.handleResponse("success", "Distributor Item ID '" + data + "' updated.");
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        get("/distributors/items/:itemId/cheapest", (req, res) -> {
            String result = null;
            try {
                String item = req.params(":itemId");
                String quantity = req.queryParams("quantity");
                if (quantity != null && item != null) {
                    JSONArray data = DatabaseManager.getCheapestPrice(quantity,
                            item);
                    result = Helpers.handleResponse("success", data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        delete("/inventory/items/:itemId", (req, res) -> {
            String result = null;
            try {
                String item = req.params(":itemId");
                if (item != null) {
                    Integer data = DatabaseManager.deleteItem(item);
                    result = Helpers.handleResponse("success", "Deleted Item Id : " + data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });

        delete("/distributors/:distributorId", (req, res) -> {
            String result = null;
            try {
                String distributor = req.params(":distributorId");
                if (distributor != null) {
                    Integer data = DatabaseManager.deleteDistributor(distributor);
                    result = Helpers.handleResponse("success", "Deleted Distributor Id : " + data);
                }
                res.status(200);
            } catch (Exception e) {
                result = Helpers.handleResponse("error", e);
                res.status(400);
            }
            res.body(result);
            return res.body();
        });
    }
}