package pl.bdygasinski.filewalker;

sealed interface Entry {

    record FileEntry(String value) implements Entry {}

    record DirEntry(String value) implements Entry {}
}
