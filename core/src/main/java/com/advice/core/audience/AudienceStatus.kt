package com.advice.core.audience

/**
 * Domain mapping of Play Age Signals response fields.
 *
 * [Declared]/[Supervised]/[Verified] come from `ageRangeSource` (TIER_A–D).
 * [Pending]/[Denied] come from `significantChangeStatus` when present.
 */
enum class AudienceStatus {
    Declared,
    Supervised,
    Verified,
    Pending,
    Denied,
    Unknown,
}
