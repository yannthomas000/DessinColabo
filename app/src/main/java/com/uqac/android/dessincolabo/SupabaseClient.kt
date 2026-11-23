package com.uqac.android.dessincolabo

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseManager {
    private const val SUPABASE_URL = "https://tbsaetzncjmfxcnearcn.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_SKHwwLkk3GwSKpUldJrLeA_EC4R5x8D"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}