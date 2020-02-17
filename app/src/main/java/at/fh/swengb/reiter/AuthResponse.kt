package at.fh.swengb.reiter

import com.squareup.moshi.JsonClass


@JsonClass (generateAdapter = true)


class AuthResponse(val token: String) {}