#define _CRT_SECURE_NO_WARNINGS

#include <iostream>

#include <openssl/applink.c>
#include <openssl/x509.h>
#include <openssl/bio.h>
#include <openssl/rsa.h>
#include <openssl/evp.h>

using namespace std;

#define to_days(days) (long)60*60*24*days

int main()
{
	X509* certificate = X509_new();

	X509_set_version(certificate, 0x02);

	// set serial number
	ASN1_INTEGER_set(X509_get_serialNumber(certificate), 1);

	// set field C from the issuer name
	X509_NAME_add_entry_by_txt(X509_get_issuer_name(certificate), "C", MBSTRING_ASC, (unsigned char*)"RO", -1, -1, 0);

	// set field O from the issuer name
	X509_NAME_add_entry_by_txt(X509_get_issuer_name(certificate), "O", MBSTRING_ASC, (unsigned char*)"ASE", -1, -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_issuer_name(certificate), "OU", MBSTRING_ASC,
	                           (unsigned char*)"ITC Security Master", -1, -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_issuer_name(certificate), "CN", MBSTRING_ASC, (unsigned char*)"Cazacu Andrei",
	                           -1,
	                           -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_subject_name(certificate), "C", MBSTRING_ASC, (unsigned char*)"RO", -1, -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_subject_name(certificate), "O", MBSTRING_ASC, (unsigned char*)"ASE", -1, -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_subject_name(certificate), "OU", MBSTRING_ASC,
	                           (unsigned char*)"ITC Security Master", -1, -1, 0);

	X509_NAME_add_entry_by_txt(X509_get_subject_name(certificate), "CN", MBSTRING_ASC, (unsigned char*)"Cazacu Andrei",
	                           -1, -1, 0);

	auto daysStart = 1, daysStop = 7;
	X509_gmtime_adj(X509_get_notBefore(certificate), to_days(daysStart));
	X509_gmtime_adj(X509_get_notAfter(certificate), to_days(daysStop));

	EVP_PKEY* keyPair = EVP_PKEY_new();
	RSA* rsaKey = RSA_generate_key(2048, 65535, nullptr, nullptr);
	EVP_PKEY_set1_RSA(keyPair, rsaKey);

	X509_set_pubkey(certificate, keyPair);


	X509_sign(certificate, keyPair, EVP_sha1());

	BIO* out1 = BIO_new_file("SampleCert.cer", "w");
	i2d_X509_bio(out1, certificate);
	BIO_free(out1);

	BIO* out2 = BIO_new_file("SampleCert.key", "w");
	i2d_PrivateKey_bio(out2, keyPair);
	BIO_free(out2);

	EVP_PKEY_free(keyPair);
	RSA_free(rsaKey);
	X509_free(certificate);

	return 0;
}
