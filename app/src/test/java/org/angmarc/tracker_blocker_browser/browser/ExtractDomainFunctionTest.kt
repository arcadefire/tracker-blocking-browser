package org.angmarc.tracker_blocker_browser.browser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExtractDomainFunctionTest {

    @Test
    fun `should extract an empty string when the host is an empty string`() {
        assertThat(extractDomain("")).isEqualTo("")
    }

    @Test
    fun `should extract an empty string when the host is a mangled string`() {
        assertThat(extractDomain(".com")).isEqualTo("")
    }

    @Test
    fun `should extract an empty string when the input is a random string`() {
        assertThat(extractDomain("12345")).isEqualTo("")
    }

    @Test
    fun `should extract the domain from a valid host`() {
        assertThat(extractDomain("www.theverge.com")).isEqualTo("theverge.com")
    }

    @Test
    fun `should extract the domain from a valid with host with subdomains`() {
        assertThat(extractDomain("www.something.something.theverge.com")).isEqualTo("theverge.com")
    }

    @Test
    fun `should extract the domain from a valid with host without www`() {
        assertThat(extractDomain("theverge.com")).isEqualTo("theverge.com")
    }

    @Test
    fun `should extract the domain from a valid with host with subdomains and without www`() {
        assertThat(extractDomain("something.something.theverge.com")).isEqualTo("theverge.com")
    }
}