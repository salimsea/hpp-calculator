package com.seal.hppcalculator

import android.app.Application
import com.seal.hppcalculator.data.local.HppDatabase
import com.seal.hppcalculator.data.repository.HppRepository

class HppApplication : Application() {
    val database by lazy { HppDatabase.getDatabase(this) }
    val repository by lazy { HppRepository(database.hppDao()) }
}
