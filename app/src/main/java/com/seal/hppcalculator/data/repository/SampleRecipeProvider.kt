package com.seal.hppcalculator.data.repository

import android.content.Context
import com.seal.hppcalculator.data.model.Ingredient
import com.seal.hppcalculator.data.model.ProductCost
import com.seal.hppcalculator.data.model.SampleRecipe
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object SampleRecipeProvider {

    fun getSampleRecipes(context: Context): List<SampleRecipe> {
        return try {
            val jsonString = context.assets.open("sample_recipes.json").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            }
            parseRecipesJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultFallbackRecipes()
        }
    }

    private fun parseRecipesJson(jsonString: String): List<SampleRecipe> {
        val list = mutableListOf<SampleRecipe>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.optString("id", "recipe_$i")
            val name = obj.getString("name")
            val category = obj.optString("category", "FNB")
            val targetProduction = obj.optDouble("targetProduction", 1.0)
            val marginPercent = obj.optDouble("marginPercent", 50.0)
            val description = obj.optString("description", "")
            val dailySalesTarget = obj.optInt("dailySalesTarget", 10)
            val targetDays = obj.optInt("targetDays", 30)
            val businessTips = obj.optString("businessTips", "")

            val ingredientsList = mutableListOf<Ingredient>()
            val ingArray = obj.optJSONArray("ingredients")
            if (ingArray != null) {
                for (j in 0 until ingArray.length()) {
                    val ingObj = ingArray.getJSONObject(j)
                    val ingName = ingObj.getString("name")
                    val cost = ingObj.getDouble("cost")
                    val amountUsed = ingObj.optDouble("amountUsed", 1.0)
                    val unit = ingObj.optString("unit", "satuan")

                    ingredientsList.add(
                        Ingredient(
                            id = 0,
                            name = ingName,
                            buyPrice = cost,
                            buyQty = 1.0,
                            usedQty = amountUsed,
                            unit = unit
                        )
                    )
                }
            }

            list.add(
                SampleRecipe(
                    id = id,
                    name = name,
                    category = category,
                    targetProduction = targetProduction,
                    marginPercent = marginPercent,
                    description = description,
                    dailySalesTarget = dailySalesTarget,
                    targetDays = targetDays,
                    businessTips = businessTips,
                    ingredients = ingredientsList
                )
            )
        }
        return list
    }

    private fun getDefaultFallbackRecipes(): List<SampleRecipe> {
        return listOf(
            SampleRecipe(
                id = "tahu_crispy",
                name = "Tahu Crispy Gurih",
                category = "FNB",
                targetProduction = 50.0,
                marginPercent = 65.0,
                description = "Camilan gorengan renyah tahan lama dengan modal terjangkau dan perputaran cepat untuk jualan pinggir jalan / booth.",
                dailySalesTarget = 50,
                targetDays = 30,
                businessTips = "Tahu crispy memiliki margin laba tinggi (>60%). Kunci suksesnya adalah minyak yang selalu panas dan bumbu tabur aneka rasa.",
                ingredients = listOf(
                    Ingredient(name = "Tahu Putih Segar (10 Kotak)", buyPrice = 30000.0, buyQty = 1.0, usedQty = 1.0, unit = "pack"),
                    Ingredient(name = "Tepung Terigu Protein Sedang", buyPrice = 6000.0, buyQty = 1.0, usedQty = 1.0, unit = "bungkus"),
                    Ingredient(name = "Tepung Maizena / Tapioka", buyPrice = 4000.0, buyQty = 1.0, usedQty = 1.0, unit = "bungkus"),
                    Ingredient(name = "Bumbu Kaldu & Bawang Putih", buyPrice = 6000.0, buyQty = 1.0, usedQty = 1.0, unit = "porsi"),
                    Ingredient(name = "Minyak Goreng Sawit (1 Liter)", buyPrice = 16000.0, buyQty = 1.0, usedQty = 1.0, unit = "liter"),
                    Ingredient(name = "Kantong Kertas Snack (50 pcs)", buyPrice = 10000.0, buyQty = 1.0, usedQty = 1.0, unit = "pack"),
                    Ingredient(name = "Cabai Rawit Hijau", buyPrice = 5000.0, buyQty = 1.0, usedQty = 1.0, unit = "bungkus"),
                    Ingredient(name = "Gas Elpiji & Operasional", buyPrice = 11000.0, buyQty = 1.0, usedQty = 1.0, unit = "hari")
                )
            )
        )
    }
}
