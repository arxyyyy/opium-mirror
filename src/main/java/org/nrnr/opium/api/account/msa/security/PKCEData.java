package org.nrnr.opium.api.account.msa.security;

/**
 * @param challenge
 * @param verifier
 * @author xgraza
 * @since 03/31/24
 */
public record PKCEData(String challenge, String verifier) {
}
